package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.remote.response.NovelDetailResponseDto

class NovelDetailViewModel : ViewModel() {
    // 테스트용 변수
    val dummyNovelDetail =
        NovelDetailResponseDto(
            userNovelId = 1,
            novelTitle = "철혈검가 사냥개의 회귀",
            novelImage = "https://github.com/Team-WSS/WSS-Android/assets/127238018/9904c8f8-623e-4a24-90f9-08f0144f5a1f",
            novelGenres = listOf("dummy"),
            novelGenreImage = "https://github.com/Team-WSS/WSS-Android/assets/127238018/b060e980-2be2-4f5c-9f85-f3bc2fcb9686",
            isNovelCompleted = false,
            author = "dummy",
            interestCount = 1,
            novelRating = 3.5f,
            novelRatingCount = 1,
            feedCount = 1,
            userNovelRating = 4.0f,
            readStatus = "QUIT",
            startDate = "2024-03-01",
            endDate = "2024-05-11",
            isUserNovelInterest = true,
        )

    companion object {
        const val DEFAULT_USER_NOVEL_ID = -1 // 나중에 데이터 레이어로 옮기자 얘도 같이~~
    }
}
