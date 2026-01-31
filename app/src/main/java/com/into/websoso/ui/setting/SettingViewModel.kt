package com.into.websoso.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.PushMessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
    @Inject
    constructor(
        private val pushUserRepository: PushMessageRepository,
    ) : ViewModel() {
        fun updatePushMessageEnabled() {
            viewModelScope.launch {
                runCatching {
                    pushUserRepository.saveUserPushEnabled(true)
                }
            }
        }
    }
