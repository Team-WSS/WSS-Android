package com.teamwss.websoso.ui.detailExplore.info

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailExploreInfoViewModel @Inject constructor() : ViewModel() {
    val ratings: List<Float> = listOf(3.5f, 4.0f, 4.5f, 4.8f)
}