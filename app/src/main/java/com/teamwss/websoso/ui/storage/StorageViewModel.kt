package com.teamwss.websoso.ui.storage

import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.repository.FakeStorageRepository
import com.teamwss.websoso.ui.storage.model.StorageTab
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageRepository: FakeStorageRepository,
) : ViewModel() {

    val novelsMap = mapOf(
        StorageTab.INTEREST to storageRepository.getInterestNovels(),
        StorageTab.WATCHING to storageRepository.getWatchingNovels(),
        StorageTab.WATCHED to storageRepository.getWatchedNovels(),
        StorageTab.QUITTING to storageRepository.getQuittingNovels()
    )
}