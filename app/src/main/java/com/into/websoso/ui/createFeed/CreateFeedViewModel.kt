package com.into.websoso.ui.createFeed

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.feed.repository.UpdatedFeedRepository
import com.into.websoso.domain.usecase.GetSearchedNovelsUseCase
import com.into.websoso.ui.createFeed.model.CreateFeedCategory
import com.into.websoso.ui.createFeed.model.CreatedFeedCategoryModel
import com.into.websoso.ui.createFeed.model.SearchNovelUiState
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSearchedNovelsUseCase: GetSearchedNovelsUseCase,
    private val feedRepository: UpdatedFeedRepository,
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

    /**
     * 기존 피드의 이미지 로드를 시작하고 성공 시 압축 후 첨부 목록에 추가합니다.
     */
    private fun loadFeedImages(feedId: Long) {
        loadExistImages(feedId) { result ->
            result.onSuccess { uris ->
                if (uris.isNotEmpty()) {
                    addCompressedImages(uris)
                }
            }
        }
    }

    /**
     * 피드 상세 정보를 조회하여 연관된 모든 이미지를 다운로드합니다.
     */
    private fun loadExistImages(
        feedId: Long,
        onComplete: (Result<List<Uri>>) -> Unit,
    ) {
        viewModelScope.launch {
            val result = runCatching {
                val feed = feedRepository.fetchFeed(feedId)
                downloadAllImages(feed.images)
            }
            onComplete(result)
        }
    }

    /**
     * 다수의 이미지 URL을 순차적으로 다운로드하여 Uri 리스트로 반환합니다.
     */
    private suspend fun downloadAllImages(imageUrls: List<String>): List<Uri> {
        val uris = mutableListOf<Uri>()

        imageUrls.forEach { url ->
            val uri = safeDownloadImage(url)
            uri?.let { uris.add(it) }
        }

        return uris
    }

    /**
     * 단일 이미지 URL을 안전하게 다운로드합니다.
     */
    private suspend fun safeDownloadImage(url: String): Uri? =
        runCatching {
            feedRepository.downloadImage(url).getOrThrow()
        }.onFailure {
            Log.e("CreateFeedViewModel", it.message.toString())
        }.getOrNull()

    /**
     * 내용 및 카테고리 선택 여부를 확인하여 작성 완료 버튼의 활성화 상태를 갱신합니다.
     */
    private fun updateIsActivated() {
        isActivated.value = content.value.isNullOrEmpty().not() &&
                categories.any { it.isSelected }
    }

    /**
     * 새로운 피드를 서버에 생성 요청합니다. (Repository에서 Optimistic Update 처리됨)
     */
    fun createFeed() {
        if (isUploading.value == true) return
        viewModelScope.launch {
            runCatching {
                _isUploading.value = true
                feedRepository.saveFeed(
                    relevantCategories = categories.filter { it.isSelected }
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

    /**
     * 기존에 작성된 피드 내용을 수정 요청합니다. (Repository에서 로컬 캐시 즉시 갱신 처리됨)
     */
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
                    editedFeed = content.value.orEmpty(),
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

    /**
     * 선택한 카테고리의 활성화 상태를 토글합니다.
     */
    fun updateSelectedCategory(category: String) {
        categories.forEachIndexed { index, categoryModel ->
            _categories[index] = when (categoryModel.category.enTitle == category) {
                true -> categoryModel.copy(isSelected = !categoryModel.isSelected)
                false -> return@forEachIndexed
            }
        }
        updateIsActivated()
    }

    /**
     * 입력된 텍스트를 바탕으로 소설 검색을 수행합니다.
     */
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

    /**
     * 다음 페이지의 검색된 소설 목록을 추가로 불러옵니다.
     */
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

    /**
     * 검색된 목록 중 특정 소설을 선택 상태로 변경합니다.
     */
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

    /**
     * 선택된 소설 정보를 바탕으로 뷰모델의 상태(제목, ID)를 갱신합니다.
     */
    fun updateSelectedNovel() {
        searchNovelUiState.value?.let { searchNovelUiState ->
            val novel = searchNovelUiState.novels.find { it.isSelected }
            _selectedNovelTitle.value = novel?.title.orEmpty()
            novelId = novel?.id
        }
    }

    /**
     * 소설 선택을 취소하고 상태를 초기화합니다.
     */
    fun updateSelectedNovelClear() {
        searchNovelUiState.value?.let { searchNovelUiState ->
            val novels = searchNovelUiState.novels.map { novel ->
                novel.copy(isSelected = false)
            }
            _searchNovelUiState.value = searchNovelUiState.copy(novels = novels)
            _selectedNovelTitle.value = ""
        }
    }

    /**
     * 최대 이미지 개수를 초과하지 않는지 검증한 후 이미지 추가 프로세스를 진행합니다.
     */
    fun addImages(newImages: List<Uri>) {
        val current = _attachedImages.value
        val remaining = MAX_IMAGE_COUNT - current.size

        if (remaining >= newImages.size) {
            addCompressedImages(newImages)
        } else {
            _exceedingImageCountEvent.tryEmit(Unit)
        }
    }

    /**
     * 선택한 이미지들을 압축하여 첨부 목록에 반영하며, 실패 시 재시도합니다.
     */
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

    /**
     * 첨부된 목록에서 특정 위치의 이미지를 제거합니다.
     */
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
