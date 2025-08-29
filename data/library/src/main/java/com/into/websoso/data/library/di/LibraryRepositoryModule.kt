package com.into.websoso.data.library.di

import androidx.lifecycle.SavedStateHandle
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.repository.MyLibraryRepository
import com.into.websoso.data.library.repository.UserLibraryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Provider

@Module
@InstallIn(ViewModelComponent::class)
internal object LibraryRepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideLibraryRepository(
        savedStateHandle: SavedStateHandle,
        myLibraryRepository: Provider<MyLibraryRepository>,
        userLibraryRepository: Provider<UserLibraryRepository.Factory>,
    ): LibraryRepository {
        val userId: Long? = savedStateHandle["USER_ID"]
        return if (userId == null) {
            myLibraryRepository.get()
        } else {
            userLibraryRepository
                .get()
                .create(userId)
        }
    }
}
