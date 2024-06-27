package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.NovelDetailEntity
import javax.inject.Inject

class FakeNovelDetailRepository
    @Inject
    constructor() {
        suspend fun getNovelDetail(novelId: Long): NovelDetailEntity {
            return dummyNovelDetail
        }

        private val dummyNovelDetail: NovelDetailEntity =
            NovelDetailEntity(
                userNovel =
                    NovelDetailEntity.UserNovelEntity(
                        userNovelId = 1,
                        readStatus = "QUIT",
                        startDate = "2024-03-01",
                        endDate = "2024-05-11",
                        isUserNovelInterest = false,
                        userNovelRating = 4.5f,
                    ),
                novel =
                    NovelDetailEntity.NovelEntity(
                        novelTitle = "철혈검가 사냥개의 회귀",
                        novelImage = "https://github.com/Team-WSS/WSS-Android/assets/127238018/9904c8f8-623e-4a24-90f9-08f0144f5a1f",
                        novelGenres = listOf("판타지", "액션", "모험"),
                        novelGenreImage = "https://github.com/Team-WSS/WSS-Android/assets/127238018/b060e980-2be2-4f5c-9f85-f3bc2fcb9686",
                        isNovelCompleted = false,
                        author = "최준서",
                    ),
                userRating =
                    NovelDetailEntity.UserRatingEntity(
                        interestCount = 127,
                        novelRating = 4.0f,
                        novelRatingCount = 127,
                        feedCount = 122,
                    ),
            )
    }
