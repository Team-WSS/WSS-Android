package com.into.websoso.core.common.navigator

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

interface NavigatorProvider {
    fun navigateToLoginActivity()

    fun navigateToMainActivity()

    fun navigateToOnboardingActivity()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavigatorEntryPoint {
    fun provideNavigator(): NavigatorProvider
}
