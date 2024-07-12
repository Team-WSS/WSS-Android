package com.teamwss.websoso.ui.detailExplore.keyword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailExploreKeywordViewModel @Inject constructor() : ViewModel() {
    private val _uiState: MutableLiveData<DetailExploreKeywordUiState> =
        MutableLiveData(DetailExploreKeywordUiState())
    val uiState: LiveData<DetailExploreKeywordUiState> get() = _uiState

    private val _searchWord: MutableLiveData<String> = MutableLiveData()
    val searchWord: MutableLiveData<String> get() = _searchWord

    private val _isSearchCancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchCancelButtonVisibility: LiveData<Boolean> get() = _isSearchCancelButtonVisibility

    fun updateSearchCancelButtonVisibility() {
        _isSearchCancelButtonVisibility.value = _searchWord.value.isNullOrEmpty().not()
    }

    fun updateSearchWordEmpty() {
        _searchWord.value = ""
    }

    fun updateCurrentSelectedKeywords(
        keyword: DetailExploreKeywordModel.CategoryModel.KeywordModel,
        isSelected: Boolean,
    ) {
        _uiState.value?.let { uiState ->
            val updatedCategories =
                updateCategories(uiState.keywordModel.categories, keyword, isSelected)
            val currentSelectedKeywords =
                updateSelectedKeywords(
                    uiState.keywordModel.currentSelectedKeywords,
                    keyword,
                    isSelected,
                )

            _uiState.value =
                uiState.copy(
                    keywordModel =
                    uiState.keywordModel.copy(
                        categories = updatedCategories,
                        currentSelectedKeywords = currentSelectedKeywords,
                    ),
                )
        }
    }

    private fun updateCategories(
        categories: List<DetailExploreKeywordModel.CategoryModel>,
        keyword: DetailExploreKeywordModel.CategoryModel.KeywordModel,
        isSelected: Boolean,
    ): List<DetailExploreKeywordModel.CategoryModel> {
        return categories.map { category ->
            val updatedKeywords =
                category.keywords.map { keywordInCategory ->
                    when (keywordInCategory.keywordId == keyword.keywordId) {
                        true -> keywordInCategory.copy(isSelected = isSelected)
                        false -> keywordInCategory
                    }
                }
            category.copy(keywords = updatedKeywords)
        }
    }

    private fun updateSelectedKeywords(
        currentSelectedKeywords: List<DetailExploreKeywordModel.CategoryModel.KeywordModel>,
        keyword: DetailExploreKeywordModel.CategoryModel.KeywordModel,
        isSelected: Boolean,
    ): List<DetailExploreKeywordModel.CategoryModel.KeywordModel> {
        return currentSelectedKeywords.toMutableList().apply {
            when (isSelected) {
                true -> add(keyword)
                false -> removeIf { it.keywordId == keyword.keywordId }
            }
        }
    }
}