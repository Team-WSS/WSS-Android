package com.teamwss.websoso.ui.activityDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.repository.UserFeedRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val activityRepository: UserFeedRepository,
) : ViewModel() {

    private val _userActivity = MutableLiveData<List<ActivityModel>>()
    val userActivity: LiveData<List<ActivityModel>> get() = _userActivity

    init {
//        updateMyActivities()
    }

//    private fun updateMyActivities() {
//        viewModelScope.launch {
//            runCatching {
//                activityRepository.getUserFeed()
//            }.mapCatching { activities ->
//                activities.toUi().take(5)
//            }.onSuccess { mappedActivities ->
//                _userActivity.value = mappedActivities
//            }.onFailure { exception ->
//
//            }
//        }
//    }
}
