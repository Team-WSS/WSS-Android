package com.teamwss.websoso.ui.withdraw.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawSecondViewModel @Inject constructor() : ViewModel() {
    private val _withdrawReason: MutableLiveData<String> = MutableLiveData("")
    val withdrawReason: LiveData<String> get() = _withdrawReason

    private val _isWithdrawCheckAgree: MutableLiveData<Boolean> = MutableLiveData(false)
    val isWithdrawCheckAgree: LiveData<Boolean> get() = _isWithdrawCheckAgree

    private val _isWithdrawButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isWithdrawButtonEnabled: LiveData<Boolean> get() = _isWithdrawButtonEnabled

    val etcReason: MutableLiveData<String> = MutableLiveData()

    fun updateWithdrawReason(reason: String) {
        _withdrawReason.value = reason
        updateWithdrawButtonEnabled()
    }

    fun updateIsWithdrawCheckAgree() {
        _isWithdrawCheckAgree.value = isWithdrawCheckAgree.value?.not()
        updateWithdrawButtonEnabled()
    }

    fun updateWithdrawButtonEnabled() {
        val reasonIsNotBlank: Boolean = withdrawReason.value?.isNotBlank() == true
        val isWithdrawAgree: Boolean = isWithdrawCheckAgree.value == true

        _isWithdrawButtonEnabled.value = when {
            withdrawReason.value == ETC_INPUT_REASON -> etcReason.value?.isNotBlank() == true && isWithdrawAgree
            reasonIsNotBlank && isWithdrawAgree -> true
            else -> false
        }
    }

    fun saveWithdrawReason() {
        viewModelScope.launch {
            runCatching {
                val withdrawReason = when (withdrawReason.value) {
                    ETC_INPUT_REASON -> etcReason.value
                    else -> withdrawReason.value
                }
                // TODO: API 개발 완료 시 reason을 사용하여 회원 탈퇴 진행
            }.onSuccess {
                // TODO: 성공 처리
            }.onFailure {
                // TODO: 실패 처리
            }
        }
    }

    companion object {
        private const val ETC_INPUT_REASON = "직접입력"
    }
}