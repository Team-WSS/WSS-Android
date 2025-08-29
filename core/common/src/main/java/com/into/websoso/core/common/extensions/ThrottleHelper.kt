package com.into.websoso.core.common.extensions

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@Singleton
class ThrottleHelper
    @Inject
    constructor() {
        private val mutex = Mutex()
        private var lastExecutionTime: TimeMark? = null

        suspend operator fun <T> invoke(
            durationMillis: Long = DEFAULT_THROTTLE_DURATION,
            block: suspend () -> T,
        ): T? {
            val now = TimeSource.Monotonic.markNow()
            val shouldExecute = mutex.withLock {
                val canRun = if (lastExecutionTime == null) {
                    true
                } else {
                    val elapsed = lastExecutionTime!!.elapsedNow()
                    elapsed >= durationMillis.milliseconds
                }

                if (canRun) lastExecutionTime = now

                canRun
            }

            return if (shouldExecute) block() else null
        }

        companion object {
            private const val DEFAULT_THROTTLE_DURATION = 1000L
        }
    }
