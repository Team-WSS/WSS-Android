package com.teamwss.websoso.ui.myPage.myActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.MyActivityRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.myPage.myActivity.model.ActivityModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(
    private val myActivityRepository: MyActivityRepository
) : ViewModel() {

    private val _myActivity = MutableLiveData<List<ActivityModel>>()
    val myActivity: LiveData<List<ActivityModel>> get() = _myActivity

    init {
        updateMyActivities()
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            runCatching {
                myActivityRepository.getMyActivities()
            }.mapCatching { activities ->
                activities.toUi().take(5)
            }.onSuccess { mappedActivities ->
                _myActivity.value = mappedActivities
            }.onFailure { exception ->
                Log.e("MyActivityViewModel", "Failed to load activities", exception)
            }
        }
    }
}
