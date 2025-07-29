package com.into.websoso.data.filter.di

import androidx.lifecycle.SavedStateHandle
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.repository.MyLibraryFilterRepository
import com.into.websoso.data.filter.repository.UserLibraryFilterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal object RepositoryModule {
    @Provides
    fun provideFilterRepository(
        savedStateHandle: SavedStateHandle,
        myFilterRepository: MyLibraryFilterRepository,
        userFilterRepository: UserLibraryFilterRepository,
    ): FilterRepository {
        val userId: Long? = savedStateHandle["USER_ID"]
        return if (userId == null) myFilterRepository else userFilterRepository
    }
}
