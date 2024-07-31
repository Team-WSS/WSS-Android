package com.teamwss.websoso.ui.detailExplore.keyword

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeKeywordRepository
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel.CategoryModel
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
    private val _uiState: MutableLiveData<DetailExploreKeywordUiState> =
        MutableLiveData(DetailExploreKeywordUiState())
    val uiState: LiveData<DetailExploreKeywordUiState> get() = _uiState

    private val _selectedKeywords: MutableLiveData<List<KeywordModel>> = MutableLiveData()
    val selectedKeywords: LiveData<List<KeywordModel>> get() = _selectedKeywords

    private val _searchWord: MutableLiveData<String> = MutableLiveData()
    val searchWord: MutableLiveData<String> get() = _searchWord

    private val _isSearchCancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchCancelButtonVisibility: LiveData<Boolean> get() = _isSearchCancelButtonVisibility

    private val _selectedKeywords2: MutableLiveData<List<Int>> = MutableLiveData(emptyList())
    val selectedKeywords2: LiveData<List<Int>> get() = _selectedKeywords2

    val keywords = HashMap<Int, String>()

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

                keywordsList.forEach { categories ->
                    categories.keywords.forEach { keywordEntity ->
                        keywords[keywordEntity.keywordId] = keywordEntity.keywordName
                    }
                }

                Log.d("123123", "updateKeywords: $keywords")

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

    fun updateCurrentSelectedKeywords(
        keyword: KeywordModel,
        isSelected: Boolean,
    ) {
        _uiState.value?.let { uiState ->
            val updatedCategories =
                updateCategories(uiState.keywordModel.categories, keyword)
            val currentSelectedKeywords = selectedKeywords.value?.toList()?.let {
                updateSelectedKeywords(
                    it,
                    keyword,
                    !isSelected,
                )
            }

            _uiState.value = uiState.copy(
                keywordModel = uiState.keywordModel.copy(
                    categories = updatedCategories,
                ),
            )
            _selectedKeywords.value = currentSelectedKeywords ?: emptyList()
        }
    }

    fun updateCurrentSelectedKeywords2(keywordId: Int) {
        if (selectedKeywords2.value?.contains(keywordId) == true) {
            _selectedKeywords2.value = _selectedKeywords2.value?.filterNot {
                it == keywordId
            }
        } else {
            _selectedKeywords2.value = _selectedKeywords2.value?.let { currentList ->
                currentList.toMutableList().apply {
                    add(keywordId)
                }
            }
        }

        updateSelectedKeywords()
    }

    private fun updateSelectedKeywords() {
        val keywordModels = keywords.map { (keywordId, keywordName) ->
            KeywordModel(
                keywordId = keywordId,
                keywordName = keywordName,
                isSelected = selectedKeywords2.value?.contains(keywordId) == true,
            )
        }

        _selectedKeywords.value = keywordModels
    }

    private fun updateCategories(
        categories: List<CategoryModel>,
        keyword: KeywordModel,
    ): List<CategoryModel> {
        return categories.map { category ->
            val updatedKeywords =
                category.keywords.map { keywordInCategory ->
                    when (keywordInCategory.keywordId == keyword.keywordId) {
                        true -> keywordInCategory
                        false -> keywordInCategory
                    }
                }
            category.copy(keywords = updatedKeywords)
        }
    }

    private fun updateSelectedKeywords(
        currentSelectedKeywords: List<KeywordModel>,
        keyword: KeywordModel,
        isSelected: Boolean,
    ): List<KeywordModel> {
        return currentSelectedKeywords.toMutableList().apply {
            when (isSelected) {
                true -> add(keyword)
                false -> removeIf { it.keywordId == keyword.keywordId }
            }
        }
    }
}