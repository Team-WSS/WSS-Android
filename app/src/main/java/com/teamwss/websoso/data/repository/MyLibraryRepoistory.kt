package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.AttractivePointEntity
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import javax.inject.Inject

class MyLibraryRepository @Inject constructor() {
    fun getGenres(): List<GenrePreferenceEntity> {
        return listOf(
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "로판", 10),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "로맨스", 8),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "무협", 6),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "판타지", 4),
            GenrePreferenceEntity("https://cdn-icons-png.flaticon.com/512/660/660026.png", "코믹", 2)
        )
    }

    fun getAttractivePoints(): List<AttractivePointEntity> {
        return listOf(
            AttractivePointEntity("혐호관계", 2),
            AttractivePointEntity("궁중암투", 4),
            AttractivePointEntity("후회", 5),
            AttractivePointEntity("정치물", 9),
            AttractivePointEntity("빙의", 4)
        )
    }
}