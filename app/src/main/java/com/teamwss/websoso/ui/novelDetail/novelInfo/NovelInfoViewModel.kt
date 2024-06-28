package com.teamwss.websoso.ui.novelDetail.novelInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto

class NovelInfoViewModel : ViewModel() {
    private val _expandTextToggleVisibility: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>(false)
    val expandTextToggleVisibility: LiveData<Boolean> get() = _expandTextToggleVisibility
    private val _isExpandTextToggleEnabled: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>(true)
    val isExpandTextToggleEnabled: LiveData<Boolean> get() = _isExpandTextToggleEnabled
    private val _bodyMaxLines: MutableLiveData<Int> = MutableLiveData<Int>(DEFAULT_MAX_LINES)
    val bodyMaxLines: LiveData<Int> get() = _bodyMaxLines
    private val _dummyNovelInfo: MutableLiveData<NovelInfoResponseDto> =
        MutableLiveData<NovelInfoResponseDto>()
    val dummyNovelInfo: LiveData<NovelInfoResponseDto> get() = _dummyNovelInfo

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

    fun updateExpandTextToggle() {
        when (_isExpandTextToggleEnabled.value == true) {
            true -> {
                _isExpandTextToggleEnabled.value = false
                _bodyMaxLines.value = Int.MAX_VALUE
            }

            false -> {
                _isExpandTextToggleEnabled.value = true
                _bodyMaxLines.value = DEFAULT_MAX_LINES
            }
        }
    }

    fun updateExpandTextToggleVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ) {
        _expandTextToggleVisibility.value = lineCount >= DEFAULT_MAX_LINES && ellipsisCount > 0
    }

    companion object {
        private const val DEFAULT_MAX_LINES = 3
    }
}
