package com.teamwss.websoso.ui.createFeed

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor() : ViewModel() {
    private val selectedCategories: MutableList<Int> = mutableListOf()
    val isActivated: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isSpoiled: MutableLiveData<Boolean> = MutableLiveData(false)
    val content: MutableLiveData<String> = MutableLiveData("")

    init {
        isActivated.addSource(content) { updateIsActivated() }
    }

    fun updateSelectedCategory(categoryId: Int) {
        when (selectedCategories.contains(categoryId)) {
            true -> selectedCategories.remove(categoryId)
            false -> selectedCategories.add(categoryId)
        }

        updateIsActivated()
    }

    private fun updateIsActivated() {
        isActivated.value = content.value.isNullOrEmpty().not() && selectedCategories.isNotEmpty()
    }
}
