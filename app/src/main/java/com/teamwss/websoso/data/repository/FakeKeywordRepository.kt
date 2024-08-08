package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.KeywordsEntity
import javax.inject.Inject

class FakeKeywordRepository @Inject constructor() {

    fun fetchKeyword(): List<KeywordsEntity.CategoryEntity> {
        return listOf(
            KeywordsEntity.CategoryEntity(
                categoryName = "세계관",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 1,
                        keywordName = "이세계",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 2,
                        keywordName = "현대",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 3,
                        keywordName = "서양풍/중세시대",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 4,
                        keywordName = "SF",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 7,
                        keywordName = "실존역사",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 17,
                        keywordName = "로맨스",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 18,
                        keywordName = "판타지",
                    ),
                ),
            ),
            KeywordsEntity.CategoryEntity(
                categoryName = "소재",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 123,
                        keywordName = "웹툰화",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 124,
                        keywordName = "드라마화",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 153,
                        keywordName = "환생",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 154,
                        keywordName = "빙의",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 155,
                        keywordName = "회귀",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 192,
                        keywordName = "삼국지",
                    ),
                ),
            ),
            KeywordsEntity.CategoryEntity(
                categoryName = "캐릭터",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 234,
                        keywordName = "영웅",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 235,
                        keywordName = "악당/빌런",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 236,
                        keywordName = "먼치킨",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 237,
                        keywordName = "천재",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 238,
                        keywordName = "회귀",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 239,
                        keywordName = "삼국지",
                    ),
                ),
            ),
            KeywordsEntity.CategoryEntity(
                categoryName = "관계",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 240,
                        keywordName = "친구",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 241,
                        keywordName = "동료",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 242,
                        keywordName = "료동",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 243,
                        keywordName = "구친",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 244,
                        keywordName = "첫사랑",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 245,
                        keywordName = "막사랑",
                    ),
                ),
            ),
            KeywordsEntity.CategoryEntity(
                categoryName = "분위기/전개",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 300,
                        keywordName = "뻔한",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 301,
                        keywordName = "반전있는",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 302,
                        keywordName = "탄탄한",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 303,
                        keywordName = "힐링되는",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 304,
                        keywordName = "현실적인",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 305,
                        keywordName = "정치적인",
                    ),
                    KeywordsEntity.CategoryEntity.KeywordEntity(
                        keywordId = 306,
                        keywordName = "일상적인",
                    ),
                ),
            ),
        )
    }
}