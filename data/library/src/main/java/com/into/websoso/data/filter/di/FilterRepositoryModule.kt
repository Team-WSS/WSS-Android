package com.into.websoso.data.filter.di

import androidx.lifecycle.SavedStateHandle
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.repository.MyLibraryFilterRepository
import com.into.websoso.data.filter.repository.UserLibraryFilterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Provider

@Module
@InstallIn(ViewModelComponent::class)
internal object FilterRepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideFilterRepository(
        savedStateHandle: SavedStateHandle,
        myFilterRepository: Provider<MyLibraryFilterRepository>,
        userFilterRepository: Provider<UserLibraryFilterRepository>,
    ): FilterRepository {
        val userId: Long? = savedStateHandle["USER_ID"]
        return if (userId == null) myFilterRepository.get() else userFilterRepository.get()
    }
}
