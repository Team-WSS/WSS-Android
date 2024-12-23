package com.into.websoso.common.util.tracker

interface Tracker {
    fun trackEvent(
        eventName: String,
        properties: Map<String, Any?> = emptyMap(),
    )
}