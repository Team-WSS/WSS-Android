package com.teamwss.websoso.ui.main.myPage.myActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.teamwss.websoso.ui.main.myPage.myActivity.model.MyActivityUiState
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _myActivityUiState: MutableLiveData<MyActivityUiState> = MutableLiveData(MyActivityUiState())
    val myActivityUiState: LiveData<MyActivityUiState> get() = _myActivityUiState

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
            _myActivityUiState.value = _myActivityUiState.value?.copy(isLoading = true)
            runCatching {
                userRepository.fetchMyActivities(
                    lastFeedId.value ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _myActivityUiState.value = _myActivityUiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() }.take(ACTIVITY_LIMIT_COUNT),
                )
                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }.onFailure {
                _myActivityUiState.value = _myActivityUiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }

    fun updateLike(selectedFeedId: Long, isLiked: Boolean, updatedLikeCount: Int) {
        myActivityUiState.value?.let { myActivityUiState ->
            val selectedFeed = myActivityUiState.activities.find { activityModel ->
                activityModel.feedId == selectedFeedId
            } ?: throw IllegalArgumentException()

            if (selectedFeed.isLiked == isLiked) return

            viewModelScope.launch {
                runCatching {
                    feedRepository.saveLike(selectedFeed.isLiked, selectedFeedId)
                }.onSuccess {
                    _myActivityUiState.value = myActivityUiState.copy(
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
                    _myActivityUiState.value = myActivityUiState.copy(
                        isLoading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateRemovedFeed(feedId: Long) {
        viewModelScope.launch {
            _myActivityUiState.value = _myActivityUiState.value?.copy(isLoading = true)
            runCatching {
                feedRepository.saveRemovedFeed(feedId)
            }.onSuccess {
                _myActivityUiState.value = _myActivityUiState.value?.copy(
                    isLoading = false,
                    activities = _myActivityUiState.value?.activities?.filter { it.feedId != feedId } ?: emptyList(),
                )
            }.onFailure {
                _myActivityUiState.value = _myActivityUiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }

    companion object {
        const val ACTIVITY_LOAD_SIZE = 10
        const val ACTIVITY_LIMIT_COUNT = 5
    }
}
