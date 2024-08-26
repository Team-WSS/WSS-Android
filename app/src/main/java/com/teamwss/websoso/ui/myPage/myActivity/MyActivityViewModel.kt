package com.teamwss.websoso.ui.myPage.myActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.MyActivityRepository
import com.teamwss.websoso.ui.myPage.myActivity.model.ActivityModel
import com.teamwss.websoso.ui.myPage.myActivity.model.Genres
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(private val myActivityRepository: MyActivityRepository) :
    ViewModel() {
    private val _myActivity = MutableLiveData<List<ActivityModel>>()
    val myActivity: LiveData<List<ActivityModel>> get() = _myActivity

    init {
        updateMyActivities()
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            val activities = myActivityRepository.getMyActivities().take(5).map { entity ->
                ActivityModel(
                    feedId = entity.feedId,
                    userId = entity.userId,
                    profileImg = entity.profileImg,
                    nickname = entity.nickname,
                    isSpoiler = entity.isSpoiler,
                    feedContent = entity.feedContent,
                    createdDate = formatDate(entity.createdDate),
                    isModified = entity.isModified,
                    isLiked = entity.isLiked,
                    likeCount = entity.likeCount,
                    commentCount = entity.commentCount,
                    novelId = entity.novelId ?: 0,
                    title = entity.title ?: "",
                    novelRatingCount = entity.novelRatingCount ?: 0,
                    novelRating = entity.novelRating ?: 0.0f,
                    relevantCategories = translateGenres(entity.relevantCategories)
                )
            }
            _myActivity.value = activities
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