package com.teamwss.websoso.ui.withdraw.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WithdrawSecondViewModel @Inject constructor() : ViewModel() {
    private val _withdrawReason: MutableLiveData<String> = MutableLiveData("ㅎㅇ")
    val withdrawReason: LiveData<String> get() = _withdrawReason

    private val _isWithdrawCheckAgree: MutableLiveData<Boolean> = MutableLiveData(false)
    val isWithdrawCheckAgree: LiveData<Boolean> get() = _isWithdrawCheckAgree

    private val _isWithdrawButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isWithdrawButtonEnabled: LiveData<Boolean> get() = _isWithdrawButtonEnabled

    fun updateWithdrawReason(reason: String) {
        _withdrawReason.value = reason
        updateWithdrawButtonEnabled()
    }

    fun updateIsWithdrawCheckAgree() {
        _isWithdrawCheckAgree.value = isWithdrawCheckAgree.value?.not()
        updateWithdrawButtonEnabled()
    }

    private fun updateWithdrawButtonEnabled() {
        when (withdrawReason.value?.isNotBlank() == true && isWithdrawCheckAgree.value == true) {
            true -> _isWithdrawButtonEnabled.value = true
            false -> _isWithdrawButtonEnabled.value = false
        }
    }
}