package com.teamwss.websoso.ui.feedDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailModel
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Error
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Loading
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Success
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _feedDetailUiState: MutableLiveData<FeedDetailUiState> = MutableLiveData(Loading)
    val feedDetailUiState: LiveData<FeedDetailUiState> get() = _feedDetailUiState

    fun updateFeedDetail(feedId: Long) {
        viewModelScope.launch {
            runCatching {
                val feed = async { feedRepository.fetchFeed(feedId) }
                val comments = async { feedRepository.fetchComments(feedId) }

                FeedDetailModel(
                    feed = feed.await().toUi(),
                    comments = comments.await().comments.map { it.toUi() },
                )
            }.onSuccess { feedDetail ->
                _feedDetailUiState.value = Success(feedDetail)
            }.onFailure {
                _feedDetailUiState.value = Error
            }
        }
    }
}
