package com.teamwss.websoso.ui.withdraw.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WithdrawSecondViewModel @Inject constructor() : ViewModel() {
    private val _withdrawReason: MutableLiveData<String> = MutableLiveData("")
    val withdrawReason: LiveData<String> get() = _withdrawReason

    private val _isWithdrawCheckAgree: MutableLiveData<Boolean> = MutableLiveData(false)
    val isWithdrawCheckAgree: LiveData<Boolean> get() = _isWithdrawCheckAgree

    private val _withdrawButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val withdrawButtonEnabled: LiveData<Boolean> get() = _withdrawButtonEnabled

    fun updateWithdrawReason(reason: String) {
        _withdrawReason.value = reason
        updateWithdrawButtonEnabled()
    }

    fun updateIsWithdrawCheckAgree() {
        _isWithdrawCheckAgree.value = isWithdrawCheckAgree.value?.not()
        updateWithdrawButtonEnabled()
    }

    private fun updateWithdrawButtonEnabled() {
        if (withdrawReason.value?.isNotBlank() == true && isWithdrawCheckAgree.value == true) {
            _withdrawButtonEnabled.value = _withdrawButtonEnabled.value?.not()
        }
    }
}