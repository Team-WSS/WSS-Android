package com.teamwss.websoso.util

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SingleEventHandler {
    private val currentTime: TimeMark get() = TimeSource.Monotonic.markNow()
    private lateinit var lastEventTime: TimeMark

    fun handle(
        timeMillis: Long = 500L,
        block: () -> Unit,
    ) {
        if (::lastEventTime.isInitialized.not() ||
            (lastEventTime + timeMillis.milliseconds).hasPassedNow()
        ) block()

        lastEventTime = currentTime
    }
}
