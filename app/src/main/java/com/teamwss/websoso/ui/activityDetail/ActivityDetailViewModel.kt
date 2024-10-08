package com.teamwss.websoso.ui.activityDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userActivity = MutableLiveData<List<ActivityModel>>()
    val userActivity: LiveData<List<ActivityModel>> get() = _userActivity

    private val _userId: MutableLiveData<Long> = MutableLiveData()
    val userId: LiveData<Long> get() = _userId

    private val _lastFeedId =  MutableLiveData<Long>(0L)
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val size: Int = ACTIVITY_SIZE

    fun updateUserId(userId: Long) {
        _userId.value = userId
        updateUserFeeds(userId)
    }

    private fun updateUserFeeds(userId: Long) {
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
        const val ACTIVITY_SIZE = 10
    }
}
