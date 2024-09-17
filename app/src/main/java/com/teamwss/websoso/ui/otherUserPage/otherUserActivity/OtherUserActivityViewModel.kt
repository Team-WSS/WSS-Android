package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserFeedRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityModel
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserActivityViewModel @Inject constructor(
    private val otherUserActivityRepository: UserFeedRepository,
) : ViewModel() {
    private val _otherUserActivity = MutableLiveData<List<ActivityModel>>()
    val otherUserActivity: LiveData<List<ActivityModel>> get() = _otherUserActivity

    init {
        updateMyActivities()
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            runCatching {
                otherUserActivityRepository.getUserFeed()
            }.mapCatching { activities ->
                activities.toUi().take(ACTIVITY_COUNT)
            }.onSuccess { mappedActivities ->
                _otherUserActivity.value = mappedActivities
            }.onFailure { exception ->

            }
        }
    }

    companion object{
        const val ACTIVITY_COUNT = 5
    }
}
