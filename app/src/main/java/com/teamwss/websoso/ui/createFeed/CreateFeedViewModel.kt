package com.teamwss.websoso.ui.createFeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.domain.usecase.GetSearchedNovelsUseCase
import com.teamwss.websoso.ui.createFeed.model.CreateFeedCategory
import com.teamwss.websoso.ui.createFeed.model.SearchNovelUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor(
    private val getSearchedNovelsUseCase: GetSearchedNovelsUseCase,
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _searchNovelUiState: MutableLiveData<SearchNovelUiState> =
        MutableLiveData(SearchNovelUiState())
    val searchNovelUiState: LiveData<SearchNovelUiState> get() = _searchNovelUiState
    private val _selectedNovelTitle: MutableLiveData<String> = MutableLiveData()
    val selectedNovelTitle: LiveData<String> get() = _selectedNovelTitle
    val isActivated: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isSpoiled: MutableLiveData<Boolean> = MutableLiveData(false)
    val content: MutableLiveData<String> = MutableLiveData("")
    private var novelId: Long? = null
    private val _selectedCategories: MutableList<Int> = mutableListOf()
    val selectedCategories: List<Int> get() = _selectedCategories.toList()
    private var searchedText = ""

    init {
        isActivated.addSource(content) { updateIsActivated() }
    }

    private fun updateIsActivated() {
        isActivated.value = content.value.isNullOrEmpty().not() && _selectedCategories.isNotEmpty()
    }

    fun dispatchFeed() {
        viewModelScope.launch {
            runCatching {
                feedRepository.postFeed(
                    relevantCategories = _selectedCategories.map {
                        CreateFeedCategory.from(it).titleEn
                    },
                    feedContent = content.value ?: "",
                    novelId = novelId,
                    isSpoiler = isSpoiled.value ?: false,
                )
            }.onSuccess { }.onFailure { }
        }
    }

    fun updateSelectedCategory(categoryId: Int) {
        when (_selectedCategories.contains(categoryId)) {
            true -> _selectedCategories.remove(categoryId)
            false -> _selectedCategories.add(categoryId)
        }

        updateIsActivated()
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

    fun updateSelectedNovel(novelId: Long) {
        searchNovelUiState.value?.let { searchNovelUiState ->
            val novels = searchNovelUiState.novels.map { novel ->
                if (novel.id == novelId) novel.copy(isSelected = !novel.isSelected)
                else novel.copy(isSelected = false)
            }
            _searchNovelUiState.value = searchNovelUiState.copy(novels = novels)
        }
    }

    fun updateSelectedNovel() {
        searchNovelUiState.value?.let { searchNovelUiState ->
            val novel = searchNovelUiState.novels.find { it.isSelected }
            _selectedNovelTitle.value = novel?.title ?: ""
            novelId = novel?.id
        }
    }

    fun updateSelectedNovelClear() {
        searchNovelUiState.value?.let { searchNovelUiState ->
            val novels = searchNovelUiState.novels.map { novel ->
                novel.copy(isSelected = false)
            }
            _searchNovelUiState.value = searchNovelUiState.copy(novels = novels)
            _selectedNovelTitle.value = ""
        }
    }
}
