package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import javax.inject.Inject

class FakeUserRepository @Inject constructor() {
    val gender: String = "MALE"

    fun getGenres(): List<GenrePreferenceEntity> {
        return listOf(
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "로판", 10),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "로맨스", 8),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "무협", 6),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "판타지", 4),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "코믹", 2)
        )
    }

    fun getNovelPreferences(): NovelPreferenceEntity {
        val attractivePoints = arrayOf(
            "character", "relationship", "material"
        )
        val keywords = arrayOf(
            NovelPreferenceEntity.KeywordEntity("혐호관계", 2),
            NovelPreferenceEntity.KeywordEntity("궁중암투", 4),
            NovelPreferenceEntity.KeywordEntity("후회", 5),
            NovelPreferenceEntity.KeywordEntity("정치물", 9),
            NovelPreferenceEntity.KeywordEntity("빙의", 4)
        )
        return NovelPreferenceEntity(attractivePoints, keywords)
    }
}
