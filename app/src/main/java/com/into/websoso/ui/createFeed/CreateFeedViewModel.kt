package com.into.websoso.ui.createFeed

import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel
    @Inject
    constructor(
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
        private val _attachedImages = MutableStateFlow<List<Uri>>(emptyList())
        val attachedImages: StateFlow<List<Uri>> get() = _attachedImages
        private val _exceedingImageCountEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val exceedingImageCountEvent: SharedFlow<Unit> get() = _exceedingImageCountEvent.asSharedFlow()
        private val _updateFeedSuccessEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val updateFeedSuccessEvent: SharedFlow<Unit> get() = _updateFeedSuccessEvent.asSharedFlow()
        private val _isUploading: MutableLiveData<Boolean> = MutableLiveData(false)
        val isUploading: LiveData<Boolean> get() = _isUploading
        val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
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
                if (feed.imageUrls.isNotEmpty()) loadFeedImages(feed.feedId)
            } ?: _categories.addAll(createCategories())

            isActivated.addSource(content) { updateIsActivated() }
        }

        private fun loadFeedImages(feedId: Long) {
            viewModelScope.launch {
                val result = loadExistImages(feedId)

                result.onSuccess { uris ->
                    if (uris.isNotEmpty()) {
                        addCompressedImages(uris)
                    }
                }
            }
        }

        private suspend fun loadExistImages(feedId: Long): Result<List<Uri>> =
            runCatching {
                val feed = feedRepository.fetchFeed(feedId)
                downloadAllImages(feed.images)
            }

        private suspend fun downloadAllImages(imageUrls: List<String>): List<Uri> =
            coroutineScope {
                imageUrls
                    .map { url ->
                        async { safeDownloadImage(url) }
                    }.awaitAll()
                    .filterNotNull()
            }

        private suspend fun safeDownloadImage(url: String): Uri? =
            runCatching {
                feedRepository.downloadImage(url).getOrThrow()
            }.onFailure {
                Log.e("CreateFeedViewModel", it.message.toString())
            }.getOrNull()

        private fun updateIsActivated() {
            isActivated.value = content.value.isNullOrEmpty().not() &&
                categories.any { it.isSelected }
        }

        fun createFeed() {
            if (isUploading.value == true) return
            viewModelScope.launch {
                runCatching {
                    _isUploading.value = true
                    feedRepository.saveFeed(
                        relevantCategories = categories
                            .filter { it.isSelected }
                            .map { it.category.enTitle },
                        feedContent = content.value.orEmpty(),
                        novelId = novelId,
                        isSpoiler = isSpoiled.value ?: false,
                        isPublic = isPublic.value ?: true,
                        images = attachedImages.value,
                    )
                }.onSuccess {
                    _isUploading.value = false
                    _updateFeedSuccessEvent.emit(Unit)
                }.onFailure {
                    _isUploading.value = false
                }
            }
        }

        fun editFeed(feedId: Long) {
            if (isUploading.value == true) return
            viewModelScope.launch {
                runCatching {
                    _isUploading.value = true
                    feedRepository.saveEditedFeed(
                        feedId = feedId,
                        relevantCategories = categories
                            .filter { it.isSelected }
                            .map { it.category.enTitle },
                        feedContent = content.value.orEmpty(),
                        novelId = novelId,
                        isSpoiler = isSpoiled.value ?: false,
                        isPublic = isPublic.value ?: true,
                        images = attachedImages.value,
                    )
                }.onSuccess {
                    _isUploading.value = false
                    _updateFeedSuccessEvent.emit(Unit)
                }.onFailure {
                    _isUploading.value = false
                }
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
                                if (novel.id == novelId) {
                                    novel
                                        .toUi()
                                        .let { it.copy(isSelected = !it.isSelected) }
                                } else {
                                    novel.toUi()
                                }
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
                                if (novel.id == novelId) {
                                    novel
                                        .toUi()
                                        .let { it.copy(isSelected = !it.isSelected) }
                                } else {
                                    novel.toUi()
                                }
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
                    if (novel.id == novelId) {
                        novel.copy(isSelected = !novel.isSelected)
                    } else {
                        novel.copy(isSelected = false)
                    }
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

        fun addImages(newImages: List<Uri>) {
            val current = _attachedImages.value
            val remaining = MAX_IMAGE_COUNT - current.size

            if (remaining >= newImages.size) {
                addCompressedImages(newImages)
            } else {
                _exceedingImageCountEvent.tryEmit(Unit)
            }
        }

        private fun addCompressedImages(
            newImages: List<Uri>,
            retryCount: Int = 0,
        ) {
            if (retryCount > MAX_RETRY_COUNT) return

            viewModelScope.launch {
                runCatching {
                    feedRepository.compressImages(newImages)
                }.onSuccess { compressedImages ->
                    _attachedImages.value = attachedImages.value + compressedImages
                }.onFailure {
                    addCompressedImages(newImages, retryCount + 1)
                }
            }
        }

        fun removeImage(index: Int) {
            attachedImages.value
                .toMutableList()
                .also { image -> if (index in image.indices) image.removeAt(index) }
                .let { _attachedImages.value = it }
        }

        companion object {
            const val MAX_IMAGE_COUNT: Int = 5
            private const val MAX_RETRY_COUNT: Int = 3
        }
    }
