package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.FeedsRequestDto
import com.teamwss.websoso.data.remote.response.FeedResponseDto
import com.teamwss.websoso.data.remote.response.FeedsResponseDto
import kotlinx.coroutines.delay

object FakeApi {

    val Fixture = FeedsResponseDto(
        category = "all",
        isLoadable = true,
        feedsResponseDto = List(43) {
            // 픽스쳐 라이브러리 적용
            FeedResponseDto(
                avatarImage = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1",
                commentCount = (it + 1),
                createdDate = "3월 21일",
                feedContent = "판소추천해요!\n" +
                        "완결난지는 좀 되었는데 추천합니다. scp 같은 이상현상 물품을 모아놓은 창고를 관리하는 주인공입니다. 배경은 현대아니라 판타지세계라 더 독특해요. 성좌채팅이 있어서 호불호 갈릴 수 있지만 이것도 스토리성이 있는 부분이라 후 잘 모르겠쒀요",
                feedId = (it + 1).toLong(),
                isLiked = it % 2 == 0,
                isModified = it % 3 == 0,
                isMyFeed = it % 2 == 0,
                isSpoiler = it % 5 == 0,
                likeCount = 10 + (it + 1),
                nickname = "로판처돌이${(it + 1)}",
                novelId = (it + 1).toLong(),
                novelRating = 3.7f,
                novelRatingCount = 10 + (it + 1),
                relevantCategories = listOf("로코", "로맨스"),
                title = "눈물의 여왕${(it + 1)}",
                userId = (it + 1).toLong()
            )
        }
    )

    suspend fun getFeeds(
        category: String?,
        feedsRequestDto: FeedsRequestDto,
    ): FeedsResponseDto {
        delay(500)

        val from = feedsRequestDto.lastFeedId.toInt()
        val to = when (feedsRequestDto.lastFeedId + 10 > Fixture.feedsResponseDto.size) {
            true -> Fixture.feedsResponseDto.size
            false -> (feedsRequestDto.lastFeedId + feedsRequestDto.size).toInt()
        }

        return when {
            to >= Fixture.feedsResponseDto.size -> Fixture.copy(
                isLoadable = false,
                feedsResponseDto = Fixture.feedsResponseDto.subList(from, to),
            )

            category != null -> {
                FeedsResponseDto(
                    category = category,
                    isLoadable = false,
                    feedsResponseDto = emptyList(),
                )
            }

            else -> Fixture.copy(
                feedsResponseDto = Fixture.feedsResponseDto.subList(from, to),
            )
        }
    }
}
