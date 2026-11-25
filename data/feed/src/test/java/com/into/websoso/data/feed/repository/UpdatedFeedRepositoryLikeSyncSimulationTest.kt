package com.into.websoso.data.feed.repository

import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.core.network.datasource.feed.model.response.FeedsResponseDto
import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.store.PendingFeedLikeStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.random.Random

/**
 * 좋아요 동기화 실패 주입 시뮬레이션.
 *
 * 시드 고정 난수로 요청마다 성공·실패(서버 미반영)·타임아웃(서버 반영 후 응답 유실)·지연 응답을 주입하고,
 * 탭·조회 지연·화면 이탈 동기화·재시도·앱 종료(저장소만 남기고 Repository 재생성)를 섞어 반복한다.
 * 모든 재시도가 끝난 뒤 피드마다 서버 상태가 사용자의 마지막 선택과 같은지, 그리고 진행 중에
 * 화면이 사용자의 마지막 선택보다 오래된 값으로 되돌아간 적이 있는지를 센다.
 *
 * 설정은 [SimulationConfig] 기본값을 쓰고, 환경 변수 LIKE_SIM_*로 바꿀 수 있다.
 * 결과 표는 표준 출력(build/test-results의 system-out)에 남는다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UpdatedFeedRepositoryLikeSyncSimulationTest {
    @Test
    fun `무작위 실패와 앱 종료 속에서도 사용자의 마지막 선택이 최종 서버 상태로 남는다`() {
        val config = SimulationConfig.fromEnvironment()
        val current = runSimulation(config, Structure.CURRENT)
        val legacy = runSimulation(config, Structure.LEGACY)
        println(report(config, current, legacy))

        assertEquals("현재 구조 최종 불일치: ${current.mismatches}", 0, current.mismatches.values.sum())
        assertEquals("현재 구조 화면 되돌림: ${current.reverts}", 0, current.reverts.values.sum())
        // 이전 구조의 문제를 잡지 못하면 시뮬레이션이 약해진 것이다.
        assertTrue("이전 구조에서 불일치를 검출하지 못함", legacy.mismatches.values.sum() > 0)
    }

    private fun runSimulation(
        config: SimulationConfig,
        structure: Structure,
    ): Tally {
        val tally = Tally()
        repeat(config.iterations) { episode ->
            runEpisode(config, structure, config.seed + episode, tally)
        }
        return tally
    }

    private fun runEpisode(
        config: SimulationConfig,
        structure: Structure,
        episodeSeed: Long,
        tally: Tally,
    ) = runTest {
        // 사용자 행동·시간 난수와 요청 결과 난수를 분리해, 두 구조가 같은 사용자 시나리오를 받게 한다.
        val scenario = Random(episodeSeed)
        val faults = FaultInjector(config, Random(episodeSeed xor FAULT_SEED_SALT), tally)
        val clock = EventClock()
        val initialLikes = (1L..config.feedCount).associateWith { scenario.nextBoolean() }
        val server = SimServer(clock, initialLikes, (1L..config.feedCount).associateWith { scenario.nextInt(0, 20) })
        val disk = FakePendingFeedLikeStore()
        val choices = mutableMapOf<Long, UserChoice>()

        fun boot(): AppSession {
            val process = AppProcess(this)
            val api = SimFeedApi(server, process, faults)
            val store = ProcessBoundStore(disk, process)
            val subject = when (structure) {
                Structure.CURRENT -> CurrentSubject(createRepository(api, store))
                Structure.LEGACY -> LegacySubject(LegacyFeedLikeRepository(api, StandardTestDispatcher(testScheduler)))
            }
            // 화면이 받는 모든 목록 값을 즉시 검사한다.
            process.uiScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                subject.feeds.collect { feeds -> checkReverts(feeds, choices, tally) }
            }
            process.uiScope.launch { subject.refresh() }
            return AppSession(process, subject)
        }

        var session = boot()
        runCurrent()

        repeat(config.stepsPerIteration) {
            when (config.pickAction(scenario)) {
                Action.TOGGLE -> {
                    tally.taps++
                    val feedId = scenario.nextLong(1L, config.feedCount + 1L)
                    val shown = session.subject.feeds.value
                        .find { it.id == feedId }
                    if (shown != null) {
                        choices[feedId] = UserChoice(isLiked = !shown.isLiked, sequence = clock.next())
                        session.subject.toggle(feedId)
                    }
                }

                Action.FETCH -> {
                    session.process.uiScope.launch { session.subject.refresh() }
                }

                Action.SYNC -> {
                    session.subject.syncOnScreenExit()
                }

                Action.RETRY -> {
                    session.subject.retry()
                }

                Action.WAIT -> {
                    advanceTimeBy(scenario.nextLong(500L, 3_000L))
                }

                Action.KILL -> {
                    tally.kills++
                    if (scenario.nextBoolean()) session.subject.syncOnScreenExit()
                    runCurrent()
                    session.process.kill()
                    choices.values.forEach { it.restartedSince = true }
                    advanceTimeBy(config.restartGapMs)
                    session = boot()
                }
            }
            advanceTimeBy(scenario.nextLong(0L, config.maxStepGapMs + 1))
            runCurrent()
        }

        // 모든 재시도: 주입을 끄고 화면 이탈 동기화·수동 재시도·새로고침을 반복한다.
        faults.enabled = false
        repeat(SETTLE_ROUNDS) {
            session.subject.syncOnScreenExit()
            session.subject.retry()
            advanceUntilIdle()
            session.process.uiScope.launch { session.subject.refresh() }
            advanceUntilIdle()
        }
        session.process.kill()

        initialLikes.forEach { (feedId, initial) ->
            val choice = choices[feedId]
            val desired = choice?.isLiked ?: initial
            if (server.isLiked(feedId) != desired) {
                tally.mismatches.increment(server.classifyMismatch(feedId, desired, choice))
            }
        }
        tally.episodes++
    }

    private fun checkReverts(
        feeds: List<FeedEntity>,
        choices: Map<Long, UserChoice>,
        tally: Tally,
    ) {
        feeds.forEach { feed ->
            val choice = choices[feed.id] ?: return@forEach
            if (feed.isLiked != choice.isLiked && !choice.reverted) {
                // 같은 선택에 대한 되돌림은 한 번만 센다.
                choice.reverted = true
                tally.reverts.increment(if (choice.restartedSince) RevertType.AFTER_RESTART else RevertType.STALE_FETCH)
            }
        }
    }

    private fun report(
        config: SimulationConfig,
        current: Tally,
        legacy: Tally,
    ): String =
        buildString {
            appendLine("=== 좋아요 동기화 실패 주입 시뮬레이션 ===")
            appendLine("시드: ${config.seed}")
            appendLine("반복: ${config.iterations}회 x ${config.stepsPerIteration}단계, 피드 ${config.feedCount}개")
            appendLine(
                "요청 결과 가중치: 성공 ${config.successWeight} / 실패(미반영) ${config.failWeight} / " +
                    "타임아웃(반영·응답 유실) ${config.timeoutWeight} / 지연 응답 ${config.delayedWeight}",
            )
            appendLine("느린 조회 비율: ${config.slowFetchRate}, 행동 가중치: ${config.actionWeights}")
            appendLine()
            listOf("현재 구조" to current, "이전 구조(7de9b6497)" to legacy).forEach { (name, tally) ->
                appendLine("[$name]")
                appendLine("  탭 ${tally.taps}회, 앱 종료 ${tally.kills}회, 조회 ${tally.fetches}회(느린 조회 ${tally.slowFetches})")
                appendLine("  주입된 좋아요 요청 결과: ${tally.outcomes.toSortedMap()}")
                val mismatchByType = MismatchType.entries.associateWith { tally.mismatches[it] ?: 0 }
                val revertByType = RevertType.entries.associateWith { tally.reverts[it] ?: 0 }
                appendLine("  최종 불일치: ${tally.mismatches.values.sum()}건 $mismatchByType")
                appendLine("  화면 되돌림: ${tally.reverts.values.sum()}건 $revertByType")
            }
        }

    private companion object {
        const val FAULT_SEED_SALT = 0x5EED_FA17L
        const val SETTLE_ROUNDS = 3
    }
}

internal data class SimulationConfig(
    val seed: Long = 20_261_125L,
    val iterations: Int = 2_000,
    val stepsPerIteration: Int = 30,
    val feedCount: Int = 4,
    val successWeight: Int = 55,
    val failWeight: Int = 15,
    val timeoutWeight: Int = 15,
    val delayedWeight: Int = 15,
    val slowFetchRate: Double = 0.3,
    val actionWeights: Map<Action, Int> = mapOf(
        Action.TOGGLE to 45,
        Action.FETCH to 15,
        Action.SYNC to 12,
        Action.RETRY to 8,
        Action.WAIT to 12,
        Action.KILL to 8,
    ),
    /** 서버가 요청을 받은 뒤 반영하기까지의 최대 시간. 앱 재시작 간격보다 짧아야 한다. */
    val maxProcessingDelayMs: Long = 50L,
    val restartGapMs: Long = 300L,
    val maxStepGapMs: Long = 300L,
) {
    init {
        require(maxProcessingDelayMs < restartGapMs) { "종료된 프로세스의 요청은 재시작 전에 서버에 반영된다고 가정한다." }
    }

    fun pickAction(random: Random): Action {
        var roll = random.nextInt(actionWeights.values.sum())
        actionWeights.forEach { (action, weight) ->
            if (roll < weight) return action
            roll -= weight
        }
        error("unreachable")
    }

    companion object {
        fun fromEnvironment(): SimulationConfig {
            fun env(name: String): String? = System.getenv("LIKE_SIM_$name")
            val default = SimulationConfig()
            return default.copy(
                seed = env("SEED")?.toLong() ?: default.seed,
                iterations = env("ITERATIONS")?.toInt() ?: default.iterations,
                successWeight = env("SUCCESS")?.toInt() ?: default.successWeight,
                failWeight = env("FAIL")?.toInt() ?: default.failWeight,
                timeoutWeight = env("TIMEOUT")?.toInt() ?: default.timeoutWeight,
                delayedWeight = env("DELAYED")?.toInt() ?: default.delayedWeight,
                slowFetchRate = env("SLOW_FETCH")?.toDouble() ?: default.slowFetchRate,
            )
        }
    }
}

internal enum class Action { TOGGLE, FETCH, SYNC, RETRY, WAIT, KILL }

private enum class Structure { CURRENT, LEGACY }

private enum class Outcome { SUCCESS, FAIL, TIMEOUT, DELAYED }

private enum class MismatchType {
    /** 마지막 선택 뒤 그 값을 담은 요청이 한 번도 나가지 않았고, 그 사이 앱이 종료됐다. */
    LOST_ON_RESTART,

    /** 마지막 선택 뒤 그 값을 담은 요청이 한 번도 나가지 않았다. */
    NEVER_SENT,

    /** 보냈지만 서버에 반영되지 않았고, 다시 보내지 않았다. */
    FAILED_NOT_RETRIED,

    /** 마지막 선택이 서버에 반영된 뒤 반대 값 요청이 늦게 반영돼 덮어썼다. */
    OVERWRITTEN,
}

private enum class RevertType {
    /** 같은 프로세스에서 늦게 도착한 조회 응답 등으로 화면이 이전 값으로 돌아갔다. */
    STALE_FETCH,

    /** 앱을 다시 켠 뒤 화면이 이전 값으로 돌아갔다. */
    AFTER_RESTART,
}

private class UserChoice(
    val isLiked: Boolean,
    val sequence: Long,
) {
    var restartedSince: Boolean = false
    var reverted: Boolean = false
}

private class Tally {
    var episodes = 0
    var taps = 0
    var kills = 0
    var fetches = 0
    var slowFetches = 0
    val outcomes = mutableMapOf<Outcome, Int>()
    val mismatches = mutableMapOf<MismatchType, Int>()
    val reverts = mutableMapOf<RevertType, Int>()
}

private fun <K> MutableMap<K, Int>.increment(key: K) {
    this[key] = (this[key] ?: 0) + 1
}

/** 사용자 탭과 서버 이벤트의 선후를 비교하기 위한 공용 순번. */
private class EventClock {
    private var sequence = 0L

    fun next(): Long = ++sequence
}

private class LikeAttempt(
    val feedId: Long,
    val isLiked: Boolean,
    val sentAt: Long,
) {
    var appliedAt: Long? = null
}

private class SimServer(
    private val clock: EventClock,
    initialLikes: Map<Long, Boolean>,
    initialCounts: Map<Long, Int>,
) {
    private val likes = initialLikes.toMutableMap()
    private val counts = initialCounts.toMutableMap()
    private val attempts = mutableListOf<LikeAttempt>()

    fun isLiked(feedId: Long): Boolean = likes.getValue(feedId)

    fun snapshot(): FeedsResponseDto =
        FeedsResponseDto(
            isLoadable = false,
            feeds = likes.keys.sorted().map { id -> feedResponse(id, likes.getValue(id), counts.getValue(id)) },
        )

    fun receive(
        feedId: Long,
        isLiked: Boolean,
    ): LikeAttempt = LikeAttempt(feedId, isLiked, clock.next()).also { attempts += it }

    // POST/DELETE는 목표 상태를 설정하고, 같은 상태를 반복 설정해도 개수는 변하지 않는다.
    fun apply(attempt: LikeAttempt) {
        attempt.appliedAt = clock.next()
        if (likes.getValue(attempt.feedId) != attempt.isLiked) {
            likes[attempt.feedId] = attempt.isLiked
            counts[attempt.feedId] = counts.getValue(attempt.feedId) + if (attempt.isLiked) 1 else -1
        }
    }

    fun classifyMismatch(
        feedId: Long,
        desired: Boolean,
        choice: UserChoice?,
    ): MismatchType {
        val since = choice?.sequence ?: 0L
        val related = attempts.filter { it.feedId == feedId && it.isLiked == desired }
        return when {
            related.any { (it.appliedAt ?: 0L) > since } -> MismatchType.OVERWRITTEN
            related.any { it.sentAt > since } -> MismatchType.FAILED_NOT_RETRIED
            choice?.restartedSince == true -> MismatchType.LOST_ON_RESTART
            else -> MismatchType.NEVER_SENT
        }
    }
}

private class FaultInjector(
    private val config: SimulationConfig,
    private val random: Random,
    private val tally: Tally,
) {
    var enabled = true

    fun nextOutcome(): Outcome {
        if (!enabled) return Outcome.SUCCESS
        val weights = listOf(
            Outcome.SUCCESS to config.successWeight,
            Outcome.FAIL to config.failWeight,
            Outcome.TIMEOUT to config.timeoutWeight,
            Outcome.DELAYED to config.delayedWeight,
        )
        var roll = random.nextInt(weights.sumOf { it.second })
        val outcome = weights.first { (_, weight) -> (roll < weight).also { roll -= weight } }.first
        tally.outcomes.increment(outcome)
        return outcome
    }

    fun processingDelay(): Long = random.nextLong(5L, config.maxProcessingDelayMs + 1)

    fun responseDelay(outcome: Outcome): Long =
        when (outcome) {
            Outcome.SUCCESS, Outcome.FAIL -> random.nextLong(10L, 121L)
            Outcome.DELAYED -> random.nextLong(800L, 4_001L)
            Outcome.TIMEOUT -> TIMEOUT_MS
        }

    fun fetchDelay(): Long {
        tally.fetches++
        if (enabled && random.nextDouble() < config.slowFetchRate) {
            tally.slowFetches++
            return random.nextLong(500L, 3_001L)
        }
        return random.nextLong(10L, 121L)
    }

    private companion object {
        const val TIMEOUT_MS = 3_000L
    }
}

/** 앱 프로세스. 종료되면 그 프로세스의 코루틴은 네트워크·저장소에 닿는 순간 영원히 멈춘다. */
private class AppProcess(
    testScope: TestScope,
) {
    var alive = true
        private set
    val uiScope = CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScope.testScheduler))

    suspend fun ensureAlive() {
        if (!alive) awaitCancellation()
    }

    fun kill() {
        alive = false
        uiScope.cancel()
    }
}

private class AppSession(
    val process: AppProcess,
    val subject: LikeSyncSubject,
)

/** 조회는 요청 시점의 서버 상태를 지연 뒤에 돌려준다. 좋아요는 주입된 결과대로 반영·응답한다. */
private class SimFeedApi(
    private val server: SimServer,
    private val process: AppProcess,
    private val faults: FaultInjector,
) : FeedApi by FakeFeedApi() {
    override suspend fun getFeeds(
        feedsOption: String,
        lastFeedId: Long,
        size: Int,
    ): FeedsResponseDto {
        process.ensureAlive()
        val snapshot = server.snapshot()
        delay(faults.fetchDelay())
        process.ensureAlive()
        return snapshot
    }

    override suspend fun postLikes(feedId: Long) = like(feedId, isLiked = true)

    override suspend fun deleteLikes(feedId: Long) = like(feedId, isLiked = false)

    private suspend fun like(
        feedId: Long,
        isLiked: Boolean,
    ) {
        process.ensureAlive()
        val outcome = faults.nextOutcome()
        val attempt = server.receive(feedId, isLiked)
        // 보낸 뒤 프로세스가 종료돼도 서버는 요청을 처리한다.
        delay(faults.processingDelay())
        if (outcome != Outcome.FAIL) server.apply(attempt)
        delay(faults.responseDelay(outcome))
        process.ensureAlive()
        when (outcome) {
            Outcome.SUCCESS, Outcome.DELAYED -> Unit
            Outcome.FAIL -> throw IOException("반영 전 실패: $feedId")
            Outcome.TIMEOUT -> throw SocketTimeoutException("반영 후 응답 유실: $feedId")
        }
    }
}

/** 디스크(DataStore 대역)는 프로세스가 바뀌어도 남고, 종료된 프로세스의 쓰기는 닿지 않는다. */
private class ProcessBoundStore(
    private val disk: FakePendingFeedLikeStore,
    private val process: AppProcess,
) : PendingFeedLikeStore {
    override val pendingLikes: Flow<Map<Long, Boolean>> get() = disk.pendingLikes

    override suspend fun getPendingLikes(): Map<Long, Boolean> {
        process.ensureAlive()
        return disk.getPendingLikes()
    }

    override suspend fun updatePendingLike(
        feedId: Long,
        isLiked: Boolean,
    ) {
        process.ensureAlive()
        disk.updatePendingLike(feedId, isLiked)
    }

    override suspend fun deletePendingLike(feedId: Long) {
        process.ensureAlive()
        disk.deletePendingLike(feedId)
    }

    override suspend fun deletePendingLikeIfMatched(
        feedId: Long,
        isLiked: Boolean,
    ): Boolean {
        process.ensureAlive()
        return disk.deletePendingLikeIfMatched(feedId, isLiked)
    }

    override suspend fun claimPendingLikes(accountId: Long) {
        process.ensureAlive()
        disk.claimPendingLikes(accountId)
    }

    override suspend fun clearPendingLikes() {
        process.ensureAlive()
        disk.clearPendingLikes()
    }

    override suspend fun readResetNotice(): Boolean {
        process.ensureAlive()
        return disk.readResetNotice()
    }

    override suspend fun clearResetNotice() {
        process.ensureAlive()
        disk.clearResetNotice()
    }
}

/** 두 구조를 같은 사용자 행동으로 구동하기 위한 어댑터. */
private interface LikeSyncSubject {
    val feeds: StateFlow<List<FeedEntity>>

    suspend fun refresh()

    fun toggle(feedId: Long)

    /** 화면을 벗어날 때의 동기화. */
    fun syncOnScreenExit()

    /** 사용자의 수동 재시도. 이전 구조에는 재시도 API가 없어 같은 동기화를 다시 부른다. */
    fun retry()
}

private class CurrentSubject(
    private val repository: UpdatedFeedRepository,
) : LikeSyncSubject {
    override val feeds: StateFlow<List<FeedEntity>> = repository.sosoAllFeeds

    override suspend fun refresh() {
        repository.fetchFeeds(0L, FEED_PAGE_SIZE, "ALL")
    }

    override fun toggle(feedId: Long) = repository.toggleLikeLocal(feedId)

    override fun syncOnScreenExit() = repository.syncPendingLikes()

    override fun retry() = repository.retryPendingLikes()
}

private class LegacySubject(
    private val repository: LegacyFeedLikeRepository,
) : LikeSyncSubject {
    override val feeds: StateFlow<List<FeedEntity>> = repository.sosoAllFeeds

    override suspend fun refresh() {
        repository.fetchFeeds(0L, FEED_PAGE_SIZE, "ALL")
    }

    override fun toggle(feedId: Long) = repository.toggleLikeLocal(feedId)

    override fun syncOnScreenExit() = repository.syncDirtyFeeds()

    override fun retry() = repository.syncDirtyFeeds()
}

private const val FEED_PAGE_SIZE = 20
