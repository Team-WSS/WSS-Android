package com.into.websoso.ui.main.myPage.myActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.main.myPage.myActivity.model.MyActivityUiState
import com.into.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.into.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<MyActivityUiState> = MutableLiveData(MyActivityUiState())
    val uiState: LiveData<MyActivityUiState> get() = _uiState

    private val _lastFeedId: MutableLiveData<Long> = MutableLiveData(0L)
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val _userProfile = MutableLiveData<UserProfileModel>()
    val userProfile: LiveData<UserProfileModel> get() = _userProfile

    private val size: Int = ACTIVITY_LOAD_SIZE

    init {
        updateMyActivities()
    }

    fun updateRefreshedActivities() {
        _lastFeedId.value = 0L
        updateMyActivities()
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)
            runCatching {
                userRepository.fetchMyActivities(
                    lastFeedId.value ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() }.take(ACTIVITY_LIMIT_COUNT),
                )
                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: _lastFeedId.value
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    isError = true,
                )
            }
        }
    }

    fun updateLike(selectedFeedId: Long, isLiked: Boolean, updatedLikeCount: Int) {
        uiState.value?.let { myActivityUiState ->
            val selectedFeed = myActivityUiState.activities.find { activityModel ->
                activityModel.feedId == selectedFeedId
            } ?: throw IllegalArgumentException()

            if (selectedFeed.isLiked == isLiked) return

            viewModelScope.launch {
                runCatching {
                    feedRepository.saveLike(selectedFeed.isLiked, selectedFeedId)
                }.onSuccess {
                    _uiState.value = myActivityUiState.copy(
                        activities = myActivityUiState.activities.map { activityModel ->
                            when (activityModel.feedId == selectedFeedId) {
                                true -> activityModel.copy(
                                    isLiked = isLiked,
                                    likeCount = updatedLikeCount,
                                )

                                false -> activityModel
                            }
                        }
                    )
                }.onFailure {
                    _uiState.value = myActivityUiState.copy(
                        isLoading = false,
                        isError = true,
                    )
                }
            }
        }
    }

    fun updateRemovedFeed(feedId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)
            runCatching {
                feedRepository.saveRemovedFeed(feedId)
            }.onSuccess {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    activities = _uiState.value?.activities?.filter { it.feedId != feedId } ?: emptyList(),
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    isError = true,
                )
            }
        }
    }

    companion object {
        const val ACTIVITY_LOAD_SIZE = 10
        const val ACTIVITY_LIMIT_COUNT = 5
    }
}