package com.into.websoso.common.util.tracker.amplitude

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.autocaptureOptions
import com.into.websoso.common.util.tracker.Tracker

class AmplitudeTracker(context: Context, apiKey: String) : Tracker {
    private val amplitude =
        Amplitude(
            Configuration(
                apiKey = apiKey,
                context = context,
                autocapture =
                autocaptureOptions {
                    +sessions
                    +appLifecycles
                    +screenViews
                },
            ),
        )

    override fun trackEvent(
        eventName: String,
        properties: Map<String, Any?>,
    ) {
        amplitude.track(eventName, properties)
    }
}