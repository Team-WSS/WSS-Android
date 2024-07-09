package com.teamwss.websoso.ui.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.AttractivePointData
import com.teamwss.websoso.data.model.PreferredGenreEntity

class MyLibraryViewModel : ViewModel() {
    private val _genres = MutableLiveData<List<PreferredGenreEntity>>().apply {
        value = listOf(
            PreferredGenreEntity(R.drawable.ic_novel_detail_genre_test, "로판", 3),
            PreferredGenreEntity(R.drawable.ic_novel_detail_genre_test, "로맨스", 3),
            PreferredGenreEntity(R.drawable.ic_novel_detail_genre_test, "무협", 3),
            PreferredGenreEntity(R.drawable.ic_novel_detail_genre_test, "판타지", 3),
            PreferredGenreEntity(R.drawable.ic_novel_detail_genre_test, "코믹", 3),
            PreferredGenreEntity(R.drawable.ic_novel_detail_genre_test, "무협", 3)
        )
    }
    val genres: LiveData<List<PreferredGenreEntity>> = _genres

    private val _isGenreListVisible = MutableLiveData<Boolean>().apply { value = false }
    val isGenreListVisible: LiveData<Boolean> = _isGenreListVisible

    private val _attractivePoints = MutableLiveData<List<AttractivePointData>>().apply {
        value = listOf(
            AttractivePointData("혐호관계", 2),
            AttractivePointData("궁중암투", 4),
            AttractivePointData("후회", 5),
            AttractivePointData("정치물", 9),
            AttractivePointData("빙의", 4),
            AttractivePointData("궁중암투", 4),
            AttractivePointData("후회", 5),
            AttractivePointData("집착", 9),
            AttractivePointData("궁중암투", 4),
            AttractivePointData("후회", 5),
            AttractivePointData("집착", 9),
            AttractivePointData("궁중암투", 4),
            AttractivePointData("궁중암투", 4),
            AttractivePointData("궁중암투", 4),
            AttractivePointData("궁중암투", 4),
            AttractivePointData("궁중암투", 4)
        )
    }
    val attractivePoints: LiveData<List<AttractivePointData>> = _attractivePoints

    val genreCount: LiveData<String> = _genres.map { genres ->
        genres.sumOf { it.genreCount }.toString()
    }

    fun toggleGenreListVisibility() {
        _isGenreListVisible.value = _isGenreListVisible.value?.not()
    }
}


