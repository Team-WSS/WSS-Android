package com.into.websoso.core.common.util.tracker.di

import android.content.Context
import com.into.websoso.BuildConfig
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.common.util.tracker.amplitude.AmplitudeTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackerModule {
    @Provides
    @Singleton
    fun provideTracker(
        @ApplicationContext context: Context,
    ): Tracker = AmplitudeTracker(context, BuildConfig.AMPLITUDE_KEY)
}
