package com.into.websoso.data.library.di

import androidx.lifecycle.SavedStateHandle
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.repository.MyLibraryRepository
import com.into.websoso.data.library.repository.UserLibraryRepository.Factory
import com.into.websoso.data.library.repository.UserLibraryRepository.UserLibraryRepositoryFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Module
    @InstallIn(ViewModelComponent::class)
    abstract class UserLibraryRepositoryModule {
        @Binds
        abstract fun bindUserLibraryRepositoryFactory(userLibraryRepositoryFactory: UserLibraryRepositoryFactory): Factory
    }

    @Provides
    fun provideLibraryRepository(
        savedStateHandle: SavedStateHandle,
        myLibraryRepository: MyLibraryRepository,
        userLibraryRepository: Factory,
    ): LibraryRepository {
        val userId: Long? = savedStateHandle["USER_ID"]

        return if (userId == null) {
            myLibraryRepository
        } else {
            userLibraryRepository.create(userId)
        }
    }
}
