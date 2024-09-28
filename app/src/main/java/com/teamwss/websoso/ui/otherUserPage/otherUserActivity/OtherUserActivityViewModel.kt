package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserActivityViewModel @Inject constructor(
    private val otherUserActivityRepository: UserRepository,
) : ViewModel() {
    private val _otherUserActivity = MutableLiveData<List<ActivityModel>>()
    val otherUserActivity: LiveData<List<ActivityModel>> get() = _otherUserActivity

    private val _likeState = MutableLiveData<ActivityLikeState>()
    val likeState: LiveData<ActivityLikeState> get() = _likeState

    private val _lastFeedId = MutableLiveData<Long>().apply { value = 0L }
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val _userId: MutableLiveData<Long> = MutableLiveData()
    val userId: LiveData<Long> get() = _userId

    private val size: Int = 10

    fun updateUserId(userId: Long) {
        _userId.value = userId
        updateUserFeeds(userId)
    }

    private fun updateUserFeeds(userId: Long) {
        viewModelScope.launch {
            runCatching {
                otherUserActivityRepository.fetchUserFeeds(
                    userId = userId,
                    lastFeedId = lastFeedId.value ?: 0L,
                    size = size,
                )
            }.onSuccess { response ->
                _otherUserActivity.value = response.feeds.map { it.toUi() }.take(ACTIVITY_COUNT)
                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }.onFailure {
            }
        }
    }

    companion object {
        const val ACTIVITY_COUNT = 5
    }
}
