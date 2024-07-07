package com.teamwss.websoso.data.repository

import NovelRatingKeywordCategoriesResponseDto
import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordCategoryEntity
import com.teamwss.websoso.data.remote.response.NovelRatingKeywordCategoryResponseDto
import com.teamwss.websoso.data.remote.response.NovelRatingKeywordResponseDto
import com.teamwss.websoso.data.remote.response.NovelRatingResponseDto
import javax.inject.Inject

class FakeNovelRatingRepository @Inject constructor() {
    suspend fun fetchNovelRating(userNovelId: Long): NovelRatingEntity {
        return dummyNovelRating.toData()
    }

    suspend fun fetchNovelRatingKeywordCategories(keyword: String): List<NovelRatingKeywordCategoryEntity> {
        return if (keyword == "영웅") {
            dummySearchResult.toData()
        } else if (keyword == "공백") {
            emptyList()
        } else {
            dummyNovelRatingKeywordCategories.categories.map {
                it.toData()
            }
        }
    }

    private val dummyNovelRating =
        NovelRatingResponseDto(
            novelTitle = "당신의 이해를 돕기 위하여",
            status = "WATCHED",
            startDate = "2021-05-11",
            endDate = "2023-06-14",
            userNovelRating = 4.5f,
            attractivePoints = listOf("vibe", "character"),
            keywords =
            listOf(
                NovelRatingKeywordResponseDto(keywordId = 9, keywordName = "성장"),
                NovelRatingKeywordResponseDto(keywordId = 15, keywordName = "좀비"),
                NovelRatingKeywordResponseDto(keywordId = 36, keywordName = "호러/공포/괴담"),
            ),
        )

    private val dummyNovelRatingKeywordCategories =
        NovelRatingKeywordCategoriesResponseDto(
            categories =
            listOf(
                NovelRatingKeywordCategoryResponseDto(
                    categoryName = "소재",
                    keywords =
                    listOf(
                        NovelRatingKeywordResponseDto(keywordId = 1, keywordName = "웹툰화"),
                        NovelRatingKeywordResponseDto(keywordId = 2, keywordName = "드라마화"),
                        NovelRatingKeywordResponseDto(keywordId = 3, keywordName = "환생"),
                        NovelRatingKeywordResponseDto(keywordId = 4, keywordName = "빙의"),
                        NovelRatingKeywordResponseDto(keywordId = 5, keywordName = "회귀"),
                        NovelRatingKeywordResponseDto(keywordId = 6, keywordName = "정통"),
                        NovelRatingKeywordResponseDto(keywordId = 7, keywordName = "신화"),
                        NovelRatingKeywordResponseDto(keywordId = 8, keywordName = "삼국지"),
                        NovelRatingKeywordResponseDto(keywordId = 9, keywordName = "성장"),
                        NovelRatingKeywordResponseDto(keywordId = 10, keywordName = "모험"),
                        NovelRatingKeywordResponseDto(keywordId = 11, keywordName = "게임"),
                        NovelRatingKeywordResponseDto(keywordId = 12, keywordName = "헌터/레이드"),
                        NovelRatingKeywordResponseDto(keywordId = 13, keywordName = "성좌"),
                        NovelRatingKeywordResponseDto(keywordId = 14, keywordName = "던전"),
                        NovelRatingKeywordResponseDto(keywordId = 15, keywordName = "좀비"),
                        NovelRatingKeywordResponseDto(keywordId = 16, keywordName = "히어로/빌런"),
                        NovelRatingKeywordResponseDto(keywordId = 17, keywordName = "TS"),
                        NovelRatingKeywordResponseDto(keywordId = 18, keywordName = "상태창/시스템"),
                        NovelRatingKeywordResponseDto(keywordId = 19, keywordName = "탐동반"),
                        NovelRatingKeywordResponseDto(keywordId = 20, keywordName = "신/종교"),
                        NovelRatingKeywordResponseDto(keywordId = 21, keywordName = "초능력"),
                        NovelRatingKeywordResponseDto(keywordId = 22, keywordName = "마법/정령"),
                        NovelRatingKeywordResponseDto(keywordId = 23, keywordName = "경영"),
                        NovelRatingKeywordResponseDto(keywordId = 24, keywordName = "생존"),
                        NovelRatingKeywordResponseDto(keywordId = 25, keywordName = "전쟁"),
                        NovelRatingKeywordResponseDto(keywordId = 26, keywordName = "연애"),
                        NovelRatingKeywordResponseDto(keywordId = 27, keywordName = "결혼"),
                        NovelRatingKeywordResponseDto(keywordId = 28, keywordName = "오해/착각"),
                        NovelRatingKeywordResponseDto(keywordId = 29, keywordName = "후회"),
                        NovelRatingKeywordResponseDto(keywordId = 30, keywordName = "질투/집착"),
                        NovelRatingKeywordResponseDto(keywordId = 31, keywordName = "구원"),
                        NovelRatingKeywordResponseDto(keywordId = 32, keywordName = "복수"),
                        NovelRatingKeywordResponseDto(keywordId = 33, keywordName = "사이다"),
                        NovelRatingKeywordResponseDto(keywordId = 34, keywordName = "권선징악"),
                        NovelRatingKeywordResponseDto(keywordId = 35, keywordName = "막장"),
                        NovelRatingKeywordResponseDto(keywordId = 36, keywordName = "호러/공포/괴담"),
                        NovelRatingKeywordResponseDto(keywordId = 37, keywordName = "범죄"),
                        NovelRatingKeywordResponseDto(keywordId = 38, keywordName = "법조계"),
                        NovelRatingKeywordResponseDto(keywordId = 39, keywordName = "첩보"),
                        NovelRatingKeywordResponseDto(keywordId = 40, keywordName = "조직/암흑가"),
                        NovelRatingKeywordResponseDto(keywordId = 41, keywordName = "전문직"),
                        NovelRatingKeywordResponseDto(keywordId = 42, keywordName = "재벌"),
                        NovelRatingKeywordResponseDto(keywordId = 43, keywordName = "메디컬"),
                        NovelRatingKeywordResponseDto(keywordId = 44, keywordName = "연예계"),
                        NovelRatingKeywordResponseDto(keywordId = 45, keywordName = "SM"),
                        NovelRatingKeywordResponseDto(keywordId = 46, keywordName = "인터넷방송/BJ"),
                        NovelRatingKeywordResponseDto(keywordId = 47, keywordName = "커뮤니티"),
                        NovelRatingKeywordResponseDto(keywordId = 48, keywordName = "스포츠"),
                        NovelRatingKeywordResponseDto(keywordId = 49, keywordName = "왕족/귀족"),
                        NovelRatingKeywordResponseDto(keywordId = 50, keywordName = "육아/아기"),
                        NovelRatingKeywordResponseDto(keywordId = 51, keywordName = "임신"),
                    ),
                ),
                NovelRatingKeywordCategoryResponseDto(
                    categoryName = "캐릭터",
                    keywords =
                    listOf(
                        NovelRatingKeywordResponseDto(keywordId = 52, keywordName = "영웅"),
                        NovelRatingKeywordResponseDto(keywordId = 53, keywordName = "악당/빌런"),
                        NovelRatingKeywordResponseDto(keywordId = 54, keywordName = "먼치킨"),
                        NovelRatingKeywordResponseDto(keywordId = 55, keywordName = "천재"),
                        NovelRatingKeywordResponseDto(keywordId = 56, keywordName = "노력캐"),
                        NovelRatingKeywordResponseDto(keywordId = 57, keywordName = "인외존재"),
                        NovelRatingKeywordResponseDto(keywordId = 58, keywordName = "망나니"),
                        NovelRatingKeywordResponseDto(keywordId = 59, keywordName = "계략캐"),
                    ),
                ),
            ),
        )
    private val dummySearchResult = NovelRatingKeywordCategoriesResponseDto(
        listOf(
            NovelRatingKeywordCategoryResponseDto(
                categoryName = "캐릭터",
                keywords = listOf(
                    NovelRatingKeywordResponseDto(
                        keywordId = 52,
                        keywordName = "영웅"
                    )
                )
            )
        )
    )
}
