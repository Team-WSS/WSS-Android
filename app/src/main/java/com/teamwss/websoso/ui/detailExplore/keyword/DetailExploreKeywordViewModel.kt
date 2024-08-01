package com.teamwss.websoso.ui.detailExplore.keyword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeKeywordRepository
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel.CategoryModel.KeywordModel
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreKeywordViewModel @Inject constructor(
    private val fakeKeywordRepository: FakeKeywordRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<DetailExploreKeywordUiState> = MutableLiveData(DetailExploreKeywordUiState())
    val uiState: LiveData<DetailExploreKeywordUiState> get() = _uiState

    private val _selectedKeywords: MutableLiveData<List<KeywordModel>> = MutableLiveData(emptyList())
    val selectedKeywords: LiveData<List<KeywordModel>> get() = _selectedKeywords

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
                val detailExploreKeywordModel = DetailExploreKeywordModel(
                    categories = keywordsList.map { it.toUi() }
                )

                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    keywordModel = detailExploreKeywordModel
                )

            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateCurrentSelectedKeywords2(keyword: KeywordModel) {
        if (selectedKeywords.value?.any { it.keywordId == keyword.keywordId } == true) {
            _selectedKeywords.value =
                _selectedKeywords.value?.filterNot { it.keywordId == keyword.keywordId }
        } else {
            _selectedKeywords.value = _selectedKeywords.value?.let { currentList ->
                currentList.toMutableList().apply {
                    add(keyword)
                }
            }
        }
        updateKeywordState(keyword)
    }

    private fun updateKeywordState(keyword: KeywordModel) {
        val currentState = _uiState.value ?: return

        val updatedCategories = currentState.keywordModel.categories.map { category ->
            val updatedKeywords = category.keywords.map { existingKeyword ->
                if (existingKeyword.keywordId == keyword.keywordId) {
                    existingKeyword.copy(isSelected = !existingKeyword.isSelected)
                } else {
                    existingKeyword
                }
            }
            category.copy(keywords = updatedKeywords)
        }

        val updatedKeywordModel = currentState.keywordModel.copy(categories = updatedCategories)
        _uiState.value = currentState.copy(keywordModel = updatedKeywordModel)
    }
}