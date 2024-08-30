package com.teamwss.websoso.ui.createFeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.domain.usecase.GetSearchedNovelsUseCase
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor(
    private val getSearchedNovelsUseCase: GetSearchedNovelsUseCase,
) : ViewModel() {
    private val _searchNovelUiState: MutableLiveData<SearchNovelUiState> = MutableLiveData(
        SearchNovelUiState()
    )
    val searchNovelUiState: LiveData<SearchNovelUiState> get() = _searchNovelUiState
    private val selectedCategories: MutableList<Int> = mutableListOf()
    val isActivated: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isSpoiled: MutableLiveData<Boolean> = MutableLiveData(false)
    val content: MutableLiveData<String> = MutableLiveData("")
    private var searchedText = ""

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

    fun updateSearchedNovels(typingText: String) {
        searchNovelUiState.value?.let { searchNovelUiState ->
            if (searchedText == typingText) return

            viewModelScope.launch {
                _searchNovelUiState.value = searchNovelUiState.copy(loading = true)
                runCatching {
                    getSearchedNovelsUseCase(typingText)
                }.onSuccess { result ->
                    _searchNovelUiState.value = searchNovelUiState.copy(
                        loading = false,
                        isLoadable = result.isLoadable,
                        novelCount = result.resultCount,
                        novels = result.novels.map { it.toUi() },
                    )
                    searchedText = typingText
                }.onFailure {
                    _searchNovelUiState.value = searchNovelUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateSearchedNovels() {
        searchNovelUiState.value?.let { searchNovelUiState ->
            if (!searchNovelUiState.isLoadable) return

            viewModelScope.launch {
                runCatching {
                    getSearchedNovelsUseCase()
                }.onSuccess { result ->
                    _searchNovelUiState.value = searchNovelUiState.copy(
                        loading = false,
                        isLoadable = result.isLoadable,
                        novelCount = result.resultCount,
                        novels = result.novels.map { it.toUi() },
                    )
                }.onFailure {
                    _searchNovelUiState.value = searchNovelUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }
}

data class SearchNovelUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isLoadable: Boolean = true,
    val novelCount: Long = 0,
    val novels: List<NovelModel> = emptyList(),
)
