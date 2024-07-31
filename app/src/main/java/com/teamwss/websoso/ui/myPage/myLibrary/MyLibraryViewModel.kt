package com.teamwss.websoso.ui.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.GenrePreferenceEntity

class MyLibraryViewModel : ViewModel() {
    private val _genres = MutableLiveData<List<GenrePreferenceEntity>>().apply {
        value = listOf(
            GenrePreferenceEntity(R.drawable.ic_my_library_genre_test, "로판", 10),
            GenrePreferenceEntity(R.drawable.ic_novel_detail_genre_test, "로맨스", 8),
            GenrePreferenceEntity(R.drawable.ic_novel_detail_genre_test, "무협", 6),
            GenrePreferenceEntity(R.drawable.ic_novel_detail_genre_test, "판타지", 4),
            GenrePreferenceEntity(R.drawable.ic_novel_detail_genre_test, "코믹", 2),
            GenrePreferenceEntity(R.drawable.ic_novel_detail_genre_test, "무협", 1)
        )
    }
    val genres: LiveData<List<GenrePreferenceEntity>> = _genres
}