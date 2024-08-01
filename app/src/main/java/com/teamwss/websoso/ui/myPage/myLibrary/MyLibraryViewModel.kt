package com.teamwss.websoso.ui.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor() : ViewModel() {
    private val _genres = MutableLiveData<List<GenrePreferenceEntity>>().apply {
        value = listOf(
            GenrePreferenceEntity("https://imgur.com/bdP0jm0", "로판", 10),
            GenrePreferenceEntity("https://imgur.com/bdP0jm0", "로맨스", 8),
            GenrePreferenceEntity("https://imgur.com/bdP0jm0", "무협", 6),
            GenrePreferenceEntity("https://imgur.com/bdP0jm0", "판타지", 4),
            GenrePreferenceEntity("https://imgur.com/bdP0jm0", "코믹", 2),
            GenrePreferenceEntity("https://imgur.com/bdP0jm0", "무협", 1)
        )
    }
    val genres: LiveData<List<GenrePreferenceEntity>> = _genres

    private val _isGenreListVisible = MutableLiveData<Boolean>().apply { value = false }
    val isGenreListVisible: LiveData<Boolean> = _isGenreListVisible

    fun updateToggleGenresVisibility() {
        _isGenreListVisible.value = _isGenreListVisible.value?.not()
    }
}