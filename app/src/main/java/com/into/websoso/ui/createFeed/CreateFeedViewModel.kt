package com.into.websoso.ui.createFeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.domain.usecase.GetSearchedNovelsUseCase
import com.into.websoso.ui.createFeed.model.CreateFeedCategory
import com.into.websoso.ui.createFeed.model.CreatedFeedCategoryModel
import com.into.websoso.ui.createFeed.model.SearchNovelUiState
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSearchedNovelsUseCase: GetSearchedNovelsUseCase,
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _searchNovelUiState: MutableLiveData<SearchNovelUiState> =
        MutableLiveData(SearchNovelUiState())
    val searchNovelUiState: LiveData<SearchNovelUiState> get() = _searchNovelUiState
    private val _categories: MutableList<CreatedFeedCategoryModel> = mutableListOf()
    val categories: List<CreatedFeedCategoryModel> get() = _categories.toList()
    private val _selectedNovelTitle: MutableLiveData<String> = MutableLiveData()
    val selectedNovelTitle: LiveData<String> get() = _selectedNovelTitle
    val isActivated: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isSpoiled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPublic: MutableLiveData<Boolean> = MutableLiveData(true)
    val content: MutableLiveData<String> = MutableLiveData("")
    private var novelId: Long? = null
    private var searchedText = ""

    init {
        fun createCategories(feedCategory: List<String>? = null): List<CreatedFeedCategoryModel> =
            CreateFeedCategory.entries.map { category ->
                CreatedFeedCategoryModel(
                    category = category,
                    isSelected = feedCategory?.contains(category.krTitle) == true,
                )
            }

        savedStateHandle.get<EditFeedModel>("FEED")?.let { feed ->
            novelId = feed.novelId
            _selectedNovelTitle.value = feed.novelTitle.orEmpty()
            content.value = feed.feedContent
            isSpoiled.value = feed.isSpoiler
            isPublic.value = feed.isPublic
            _categories.addAll(createCategories(feed.feedCategory))
        } ?: _categories.addAll(createCategories())

        isActivated.addSource(content) { updateIsActivated() }
    }

    private fun updateIsActivated() {
        isActivated.value = content.value.isNullOrEmpty().not() &&
                categories.any { it.isSelected }
    }

    fun createFeed() {
        viewModelScope.launch {
            runCatching {
                feedRepository.saveFeed(
                    relevantCategories = categories.filter { it.isSelected }
                        .map { it.category.enTitle },
                    feedContent = content.value.orEmpty(),
                    novelId = novelId,
                    isSpoiler = isSpoiled.value ?: false,
                    isPublic = isPublic.value ?: true,
                )
            }.onSuccess { }.onFailure { }
        }
    }

    fun editFeed(feedId: Long) {
        viewModelScope.launch {
            runCatching {
                feedRepository.saveEditedFeed(
                    feedId = feedId,
                    relevantCategories = categories.filter { it.isSelected }
                        .map { it.category.enTitle },
                    feedContent = content.value.orEmpty(),
                    novelId = novelId,
                    isSpoiler = isSpoiled.value ?: false,
                    isPublic = isPublic.value ?: true,
                )
            }.onSuccess { }.onFailure { }
        }
    }

    fun updateSelectedCategory(category: String) {
        categories.forEachIndexed { index, categoryModel ->
            _categories[index] = when (categoryModel.category.enTitle == category) {
                true -> categoryModel.copy(isSelected = !categoryModel.isSelected)
                false -> return@forEachIndexed
            }
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
                        novels = result.novels.map { novel ->
                            if (novel.id == novelId) novel.toUi()
                                .let { it.copy(isSelected = !it.isSelected) }
                            else novel.toUi()
                        },
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
                        novels = result.novels.map { novel ->
                            if (novel.id == novelId) novel.toUi()
                                .let { it.copy(isSelected = !it.isSelected) }
                            else novel.toUi()
                        },
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
            _selectedNovelTitle.value = novel?.title.orEmpty()
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
