package com.into.websoso.core.common.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SingleEventHandler private constructor() {
    private lateinit var lastEventTime: TimeMark
    private lateinit var debounceJob: Job

    fun throttleFirst(
        timeMillis: Long = DEFAULT_TIME_MILLIS,
        event: () -> Unit,
    ) {
        if (::lastEventTime.isInitialized.not() ||
            (lastEventTime + timeMillis.milliseconds).hasPassedNow()
        ) {
            event()
        }

        lastEventTime = TimeSource.Monotonic.markNow()
    }

    fun debounce(
        timeMillis: Long = DEFAULT_TIME_MILLIS,
        coroutineScope: CoroutineScope,
        event: () -> Unit,
    ) {
        if (::debounceJob.isInitialized) debounceJob.cancel()
        debounceJob = coroutineScope.launch {
            delay(timeMillis)
            event()
        }
    }

    companion object {
        private const val DEFAULT_TIME_MILLIS = 500L

        fun from(): SingleEventHandler = SingleEventHandler()
    }
}
