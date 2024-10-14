package com.teamwss.websoso.ui.activityDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity.Companion.SOURCE_MY_ACTIVITY
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _userActivity = MutableLiveData<List<ActivityModel>>()
    val userActivity: LiveData<List<ActivityModel>> get() = _userActivity

    private val _lastFeedId =  MutableLiveData<Long>(0L)
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val size: Int = ACTIVITY_LOAD_SIZE

    var source: String?
        get() = savedStateHandle["source"]
        set(value) {
            savedStateHandle["source"] = value
        }

    var userId: Long
        get() = savedStateHandle["userId"] ?: DEFAULT_USER_ID
        set(value) {
            savedStateHandle["userId"] = value
        }

    fun updateUserActivities(userId: Long) {
        this.userId = userId
        if (source == SOURCE_MY_ACTIVITY) {
            updateMyActivities()
        } else {
            updateOtherUserActivities(userId)
        }
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchMyActivities(
                    lastFeedId.value ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _userActivity.value = response.feeds.map { it.toUi() }

                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }
        }
    }

    private fun updateOtherUserActivities(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserFeeds(
                    userId = userId,
                    lastFeedId = lastFeedId.value ?: 0L,
                    size = size,
                )
            }.onSuccess { response ->
                _userActivity.value = response.feeds.map { it.toUi() }
                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }.onFailure {
            }
        }
    }

    companion object {
        const val ACTIVITY_LOAD_SIZE = 10
        const val DEFAULT_USER_ID = -1L
    }
}
