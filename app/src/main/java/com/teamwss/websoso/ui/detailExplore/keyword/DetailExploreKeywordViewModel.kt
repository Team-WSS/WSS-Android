package com.teamwss.websoso.ui.detailExplore.keyword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeKeywordRepository
import com.teamwss.websoso.common.ui.model.CategoriesModel
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreKeywordViewModel @Inject constructor(
    private val fakeKeywordRepository: FakeKeywordRepository,
) : ViewModel() {
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

    fun updateKeywords() {
        viewModelScope.launch {
            runCatching {
                fakeKeywordRepository.fetchKeyword()
            }.onSuccess { keywordsList ->
                val categoriesModel = CategoriesModel(
                    categories = keywordsList.map { it.toUi() }
                )

                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    categories = categoriesModel.categories
                )

            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateClickedChipState(keywordId: Int) {
        val currentUiState = _uiState.value ?: return

        val updatedCategories = currentUiState.categories.map { category ->
            val updatedKeywords = category.keywords.map { existingKeyword ->
                when (existingKeyword.keywordId == keywordId) {
                    true -> existingKeyword.copy(isSelected = !existingKeyword.isSelected)
                    false -> existingKeyword
                }
            }
            category.copy(keywords = updatedKeywords)
        }
        _uiState.value = currentUiState.copy(categories = updatedCategories)
    }
}