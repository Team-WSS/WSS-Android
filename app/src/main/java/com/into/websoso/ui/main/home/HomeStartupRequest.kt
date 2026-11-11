package com.into.websoso.ui.main.home

import androidx.annotation.MainThread
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.model.PopularFeedEntity
import com.into.websoso.data.model.PopularNovelsEntity
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NovelRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** A single Splash-to-Home handoff. Only its opaque ID crosses Activity boundaries. */
@Singleton
@MainThread
class HomeStartupRequest internal constructor(
    private val sessionIdentity: suspend () -> String,
    private val fetchPopular: suspend () -> PopularNovelsEntity,
    private val fetchFeeds: suspend () -> List<PopularFeedEntity>,
    private val dispatcher: CoroutineDispatcher,
) {
    @Inject
    constructor(
        accountRepository: AccountRepository,
        novelRepository: NovelRepository,
        feedRepository: FeedRepository,
    ) : this(
        accountRepository::accessToken,
        novelRepository::fetchPopularNovels,
        feedRepository::fetchPopularFeeds,
        Dispatchers.Main.immediate,
    )

    private var pending: Pair<String, Requests>? = null
    private var generation = 0L

    suspend fun start(): String? {
        val startedGeneration = ++generation
        pending?.second?.cancel()
        pending = null
        // Reading the session is best-effort; a prefetch setup failure must not block navigation.
        val identity = try {
            sessionIdentity()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            return null
        }
        currentCoroutineContext().ensureActive()
        if (identity.isBlank() || startedGeneration != generation) return null

        val owner = SupervisorJob()
        val scope = CoroutineScope(owner + dispatcher)
        val requests = Requests(
            identity,
            sessionIdentity,
            scope.async { fetchPopular() },
            scope.async { fetchFeeds() },
        )
        owner.complete()
        return UUID.randomUUID().toString().also { pending = it to requests }
    }

    internal fun take(id: String?): Requests? {
        val entry = pending?.takeIf { it.first == id } ?: return null
        pending = null
        return entry.second
    }

    fun discard(id: String?) {
        take(id)?.cancel()
    }

    internal class Requests(
        private val identity: String,
        private val sessionIdentity: suspend () -> String,
        private val popular: Deferred<PopularNovelsEntity>,
        private val feeds: Deferred<List<PopularFeedEntity>>,
    ) {
        suspend fun popular(): PopularNovelsEntity? = await(popular)

        suspend fun feeds(): List<PopularFeedEntity>? = await(feeds)

        fun cancelPopular() = popular.cancel()

        fun cancelFeeds() = feeds.cancel()

        fun cancel() {
            cancelPopular()
            cancelFeeds()
        }

        private suspend fun <T> await(request: Deferred<T>): T? =
            try {
                if (!isSameSession()) {
                    cancel()
                    null
                } else {
                    val result = request.await()
                    if (isSameSession()) {
                        result
                    } else {
                        cancel()
                        null
                    }
                }
            } catch (cancelled: CancellationException) {
                currentCoroutineContext().ensureActive()
                null
            } catch (error: Exception) {
                // Only request failures reach here; session checks never throw.
                currentCoroutineContext().ensureActive()
                if (isSameSession()) throw error
                cancel()
                null
            } finally {
                // Cancellation/replacement of this Home section also cancels its transferred request.
                request.cancel()
            }

        // A storage read failure means the session cannot be confirmed: fall back to an ordinary Home request.
        private suspend fun isSameSession(): Boolean =
            try {
                sessionIdentity() == identity
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                false
            }
    }

    companion object {
        const val KEY = "home_startup_request_id"
    }
}
