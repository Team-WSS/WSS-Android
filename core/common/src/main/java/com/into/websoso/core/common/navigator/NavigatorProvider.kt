package com.into.websoso.core.common.navigator

import android.content.Intent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

interface NavigatorProvider {
    fun navigateToLoginActivity(startActivity: (Intent) -> Unit)

    fun navigateToMainActivity(
        startActivity: (Intent) -> Unit,
        fragmentType: String? = null,
    )

    fun navigateToOnboardingActivity(startActivity: (Intent) -> Unit)

    fun navigateToUserStorageActivity(startActivity: (Intent) -> Unit)

    fun navigateToNovelDetailActivity(
        novelId: Long,
        startActivity: (Intent) -> Unit,
    )

    fun navigateToNormalExploreActivity(startActivity: (Intent) -> Unit)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavigatorEntryPoint {
    fun provideNavigator(): NavigatorProvider
}
