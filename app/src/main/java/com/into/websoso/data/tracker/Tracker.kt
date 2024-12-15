package com.into.websoso.data.tracker

interface Tracker {
    fun trackEvent(
        eventName: String,
        properties: Map<String, Any?> = emptyMap(),
    )
}