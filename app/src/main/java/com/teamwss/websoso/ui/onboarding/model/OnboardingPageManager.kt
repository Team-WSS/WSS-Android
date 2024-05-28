package com.teamwss.websoso.ui.onboarding.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class OnboardingPageManager {
    private val _currentPage = MutableLiveData(OnboardingPage.FIRST)
    val currentPage: LiveData<OnboardingPage> = _currentPage

    private val _progressBarPercent = MutableLiveData(OnboardingPage.FIRST.progressPercent)
    val progressBarPercent: LiveData<Int> = _progressBarPercent

    private val _isBackButtonVisible = MutableLiveData(OnboardingPage.FIRST.isBackButtonVisible)
    val isBackButtonVisible: LiveData<Boolean> = _isBackButtonVisible

    private val _isSkipTextVisible = MutableLiveData(OnboardingPage.FIRST.isSkipTextVisible)
    val isSkipTextVisible: LiveData<Boolean> = _isSkipTextVisible

    fun updateNextPage() {
        _currentPage.value?.let { currentPage ->
            val nextPage = nextPage(currentPage)
            if (nextPage != currentPage) {
                _currentPage.value = nextPage
                updateUIByPage(nextPage)
            }
        }
    }

    fun updatePreviousPage() {
        _currentPage.value?.let { currentPage ->
            val previousPage = previousPage(currentPage)
            if (previousPage != currentPage) {
                _currentPage.value = previousPage
                updateUIByPage(previousPage)
            }
        }
    }

    private fun nextPage(currentPage: OnboardingPage): OnboardingPage {
        val currentIndex = OnboardingPage.pages.indexOf(currentPage)
        return if (currentIndex in OnboardingPage.pages.indices - 1) {
            OnboardingPage.pages[currentIndex + 1]
        } else {
            currentPage
        }
    }

    private fun previousPage(currentPage: OnboardingPage): OnboardingPage {
        val currentIndex = OnboardingPage.pages.indexOf(currentPage)
        return if (currentIndex > 0) {
            OnboardingPage.pages[currentIndex - 1]
        } else {
            currentPage
        }
    }

    private fun updateUIByPage(page: OnboardingPage) {
        _progressBarPercent.value = page.progressPercent
        _isBackButtonVisible.value = page.isBackButtonVisible
        _isSkipTextVisible.value = page.isSkipTextVisible
    }
}
