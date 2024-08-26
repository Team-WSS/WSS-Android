package com.teamwss.websoso.ui.myPage.myActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.MyActivitiesEntity
import com.teamwss.websoso.data.repository.MyActivityRepository
import com.teamwss.websoso.ui.myPage.myActivity.model.Genres
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(private val myActivityRepository: MyActivityRepository) :
    ViewModel() {
    private val _myActivity = MutableLiveData<List<MyActivitiesEntity.MyActivityEntity>>()
    val myActivity: LiveData<List<MyActivitiesEntity.MyActivityEntity>> get() = _myActivity

    init {
        updateMyActivities()
    }

    fun updateMyActivities() {
        viewModelScope.launch {
            val activities = myActivityRepository.getMyActivities()
            _myActivity.value = activities.take(5)
        }
    }

    fun translateGenres(relevantCategories: List<String>): String {
        return relevantCategories.joinToString(", ") { category ->
            Genres.fromString(category)?.korean ?: category
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("M월 d일", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}