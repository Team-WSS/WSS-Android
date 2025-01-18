package com.into.websoso.core.common.util.tracker

interface Tracker {
    fun trackEvent(
        eventName: String,
        properties: Map<String, Any?> = emptyMap(),
    )
}
