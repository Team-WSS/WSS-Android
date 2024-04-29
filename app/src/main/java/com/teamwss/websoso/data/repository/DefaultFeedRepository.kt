package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.FeedMapper.toData
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.remote.response.FeedResponseDto
import com.teamwss.websoso.data.remote.response.FeedsResponseDto

class DefaultFeedRepository {
    private val _cachedFeeds: MutableList<FeedEntity> = mutableListOf()
    val cachedFeeds: List<FeedEntity> get() = _cachedFeeds.toList()

    suspend fun fetchFeedsByCategory(category: String?, lastFeedId: Long, size: Int) {
//        val requestBody = FeedsRequestDto(lastFeedId = lastFeedId, size = size)
//        val result = when (category == null) {
//            true -> ServiceModule.feedApi.getFeeds(feedsRequestDto = requestBody)
//            false -> ServiceModule.feedApi.getFeedsByCategory(
//                category = category,
//                feedsRequestDto = requestBody
//            )
//        }
//
//        _cachedFeeds.addAll(result.feedsResponseDto.map { it.toData() })

        _cachedFeeds.addAll(DUMMY.feedsResponseDto.map { it.toData() })
    }

    suspend fun fetchFeedsByUserId(userId: Long, lastFeedId: Long, size: Int): List<FeedEntity>? {
        return null
    }

    suspend fun fetchFeedsByNovelId(novelId: Long, lastFeedId: Long, size: Int): List<FeedEntity>? {
        return null
    }

    fun clearCachedFeeds() {
        _cachedFeeds.clear()
    }

    companion object {
        private val DUMMY = FeedsResponseDto(
            category = "all", feedsResponseDto = listOf(
                FeedResponseDto(
                    avatarImage = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1",
                    commentCount = 22,
                    createdDate = "3월 21일",
                    feedContent = "판소추천해요!\n" +
                            "완결난지는 좀 되었는데 추천합니다. scp 같은 이상현상 물품을 모아놓은 창고를 관리하는 주인공입니다. 배경은 현대아니라 판타지세계라 더 독특해요. 성좌채팅이 있어서 호불호 갈릴 수 있지만 이것도 스토리성이 있는 부분이라 후 잘 모르겠쒀요",
                    feedId = 0L,
                    isLiked = false,
                    isModified = false,
                    isMyFeed = false,
                    isSpoiler = false,
                    likeCount = 10,
                    nickname = "로판처돌이",
                    novelId = 0L,
                    novelRating = 3.7f,
                    novelRatingCount = 10,
                    relevantCategories = listOf("로코", "로맨스"),
                    title = "눈물의 여왕",
                    userId = 0L
                ),
                FeedResponseDto(
                    avatarImage = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1",
                    commentCount = 21,
                    createdDate = "3월 22일",
                    feedContent = "여주가 세계를 구함\n" +
                            "이름이 나여주입니다ㅋㅋㅋ읽던 소설이 세계멸망엔딩나서 댓글달았다가 그 세계의 본인에게 빙의하게 되었는데 S급 힐러에 세계관 관련 중요스킬까지 얻고 시작하는 스토리. 121화 최신화 기준 큰 고구마없고 남주가 질서선 댕댕이 기여워요",
                    feedId = 1L,
                    isLiked = true,
                    isModified = false,
                    isMyFeed = false,
                    isSpoiler = false,
                    likeCount = 30,
                    nickname = "죽은군",
                    novelId = 1L,
                    novelRating = 4.0f,
                    novelRatingCount = 40,
                    relevantCategories = listOf("무협", "로맨스"),
                    title = "화산귀환",
                    userId = 1L
                )
            )
        )
    }
}
