package com.teamwss.websoso.ui.createFeed

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor() : ViewModel() {
    private val selectedCategories: MutableList<Int> = mutableListOf()

    fun updateSelectedCategory(categoryId: Int) {
        when (selectedCategories.contains(categoryId)) {
            true -> selectedCategories.remove(categoryId)
            false -> selectedCategories.add(categoryId)
        }
    }
}
