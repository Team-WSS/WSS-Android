package com.into.websoso.data.tracker.di

import android.content.Context
import com.into.websoso.BuildConfig
import com.into.websoso.data.tracker.Tracker
import com.into.websoso.data.tracker.amplitude.AmplitudeTracker
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
    ): Tracker {
        return AmplitudeTracker(context, BuildConfig.AMPLITUDE_KEY)
    }
}