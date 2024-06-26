package com.teamwss.websoso.ui.novelDetail.novelInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto

class NovelInfoViewModel : ViewModel() {
    private val _viewMoreTextVisibility = MutableLiveData<Boolean>(false)
    val viewMoreTextVisibility: MutableLiveData<Boolean> get() = _viewMoreTextVisibility
    private val _isViewMoreEnabled = MutableLiveData<Boolean>(true)
    val isViewMoreEnabled: MutableLiveData<Boolean> get() = _isViewMoreEnabled
    private val _bodyMaxLines = MutableLiveData<Int>(DEFAULT_MAX_LINES)
    val bodyMaxLines: MutableLiveData<Int> get() = _bodyMaxLines

    val dummyNovelInfo: MutableLiveData<NovelInfoResponseDto> get() = _dummyNovelInfo
    private val _dummyNovelInfo = MutableLiveData<NovelInfoResponseDto>()

    fun getDummyNovelInfo() {
        _dummyNovelInfo.value =
            NovelInfoResponseDto(
                novelDescription = "설명 어쩌구",
                platforms =
                    listOf(
                        NovelInfoResponseDto.PlatformResponseDto(
                            platformName = "네이버시리즈",
                            platformImage = "https://ssl.pstatic.net/static/nstore/ogtag_series_v2.png",
                            platformUrl = "https://www.naver.com",
                        ),
                    ),
                attractivePoints = listOf("소재", "관계", "분위기"),
                keywords =
                    listOf(
                        NovelInfoResponseDto.KeywordResponseDto(
                            keywordName = "서양풍/중세시대",
                            keywordCount = 5,
                        ),
                        NovelInfoResponseDto.KeywordResponseDto(
                            keywordName = "웹툰화",
                            keywordCount = 4,
                        ),
                        NovelInfoResponseDto.KeywordResponseDto(
                            keywordName = "동양풍/사극",
                            keywordCount = 4,
                        ),
                    ),
                watchingCount = 36,
                watchedCount = 5,
                quitCount = 2,
            )
    }

    fun onViewMoreClicked() {
        if (_isViewMoreEnabled.value == true) {
            _isViewMoreEnabled.value = false
            _bodyMaxLines.value = Int.MAX_VALUE
            return
        }
        _isViewMoreEnabled.value = true
        _bodyMaxLines.value = DEFAULT_MAX_LINES
    }

    fun initViewMoreTextVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ) {
        _viewMoreTextVisibility.value = lineCount >= DEFAULT_MAX_LINES && ellipsisCount > 0
    }

    companion object {
        const val DEFAULT_MAX_LINES = 3
    }
}
