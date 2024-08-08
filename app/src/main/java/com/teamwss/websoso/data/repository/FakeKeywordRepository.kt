package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.CategoriesEntity
import javax.inject.Inject

class FakeKeywordRepository @Inject constructor() {

    fun fetchKeyword(): List<CategoriesEntity.CategoryEntity> {
        return listOf(
            CategoriesEntity.CategoryEntity(
                categoryName = "세계관",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 1,
                        keywordName = "이세계",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 2,
                        keywordName = "현대",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 3,
                        keywordName = "서양풍/중세시대",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 4,
                        keywordName = "SF",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 7,
                        keywordName = "실존역사",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 17,
                        keywordName = "로맨스",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 18,
                        keywordName = "판타지",
                    ),
                ),
            ),
            CategoriesEntity.CategoryEntity(
                categoryName = "소재",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 123,
                        keywordName = "웹툰화",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 124,
                        keywordName = "드라마화",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 153,
                        keywordName = "환생",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 154,
                        keywordName = "빙의",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 155,
                        keywordName = "회귀",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 192,
                        keywordName = "삼국지",
                    ),
                ),
            ),
            CategoriesEntity.CategoryEntity(
                categoryName = "캐릭터",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 234,
                        keywordName = "영웅",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 235,
                        keywordName = "악당/빌런",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 236,
                        keywordName = "먼치킨",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 237,
                        keywordName = "천재",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 238,
                        keywordName = "회귀",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 239,
                        keywordName = "삼국지",
                    ),
                ),
            ),
            CategoriesEntity.CategoryEntity(
                categoryName = "관계",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 240,
                        keywordName = "친구",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 241,
                        keywordName = "동료",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 242,
                        keywordName = "료동",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 243,
                        keywordName = "구친",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 244,
                        keywordName = "첫사랑",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 245,
                        keywordName = "막사랑",
                    ),
                ),
            ),
            CategoriesEntity.CategoryEntity(
                categoryName = "분위기/전개",
                categoryImage = "https://png.pngtree.com/element_our/20190604/ourmid/pngtree-a-sharp-sword-image_1473377.jpg",
                keywords = listOf(
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 300,
                        keywordName = "뻔한",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 301,
                        keywordName = "반전있는",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 302,
                        keywordName = "탄탄한",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 303,
                        keywordName = "힐링되는",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 304,
                        keywordName = "현실적인",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 305,
                        keywordName = "정치적인",
                    ),
                    CategoriesEntity.CategoryEntity.KeywordEntity(
                        keywordId = 306,
                        keywordName = "일상적인",
                    ),
                ),
            ),
        )
    }
}