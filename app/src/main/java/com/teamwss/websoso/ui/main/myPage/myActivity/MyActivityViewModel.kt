package com.teamwss.websoso.ui.main.myPage.myActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(
    private val myActivityRepository: UserRepository,
) : ViewModel() {

    private val _myActivity = MutableLiveData<List<ActivityModel>>()
    val myActivity: LiveData<List<ActivityModel>> get() = _myActivity

    private val _lastFeedId = MutableLiveData<Long>().apply { value = 0L }
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val _userProfile = MutableLiveData<UserProfileModel>()
    val userProfile: LiveData<UserProfileModel> get() = _userProfile

    private var userId: Long = 2L

    private val size: Int = 10

    init {
        updateMyActivities()
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            runCatching {
                myActivityRepository.fetchUserFeeds(
                    userId,
                    lastFeedId.value ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _myActivity.value = response.feeds.map { it.toUi() }.take(5)

                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }
        }
    }
}