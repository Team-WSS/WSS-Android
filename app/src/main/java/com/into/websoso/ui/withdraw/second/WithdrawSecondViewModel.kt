package com.into.websoso.ui.withdraw.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.data.repository.PushMessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawSecondViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val pushMessageRepository: PushMessageRepository,
    ) : ViewModel() {
        private val _withdrawReason: MutableLiveData<String> = MutableLiveData("")
        val withdrawReason: LiveData<String> get() = _withdrawReason

        private val _isWithdrawAgreementChecked: MutableLiveData<Boolean> = MutableLiveData(false)
        val isWithdrawAgreementChecked: LiveData<Boolean> get() = _isWithdrawAgreementChecked

        private val _isWithdrawButtonEnabled: MediatorLiveData<Boolean> = MediatorLiveData(false)
        val isWithdrawButtonEnabled: LiveData<Boolean> get() = _isWithdrawButtonEnabled

        private val _isWithDrawSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
        val isWithDrawSuccess: LiveData<Boolean> get() = _isWithDrawSuccess

        val withdrawEtcReason: MutableLiveData<String> = MutableLiveData()

        val withdrawEtcReasonCount: MediatorLiveData<Int> = MediatorLiveData()

        init {
            withdrawEtcReasonCount.addSource(withdrawEtcReason) { reason ->
                withdrawEtcReasonCount.value = reason.length
            }

            _isWithdrawButtonEnabled.addSource(withdrawReason) {
                _isWithdrawButtonEnabled.value = isEnabled()
            }
            _isWithdrawButtonEnabled.addSource(isWithdrawAgreementChecked) {
                _isWithdrawButtonEnabled.value = isEnabled()
            }
            _isWithdrawButtonEnabled.addSource(withdrawEtcReason) {
                _isWithdrawButtonEnabled.value = isEnabled()
            }
        }

        private fun isEnabled(): Boolean {
            val isReasonNotBlank: Boolean = _withdrawReason.value?.isNotBlank() == true
            val isWithdrawAgreement: Boolean = _isWithdrawAgreementChecked.value == true
            val isEtcReasonValid =
                _withdrawReason.value == ETC_INPUT_REASON && withdrawEtcReason.value?.isNotBlank() == true

            return when {
                _withdrawReason.value == ETC_INPUT_REASON -> isEtcReasonValid && isWithdrawAgreement
                isReasonNotBlank && isWithdrawAgreement -> true
                else -> false
            }
        }

        fun updateWithdrawReason(reason: String) {
            _withdrawReason.value = reason
        }

        fun updateIsWithdrawCheckAgreement() {
            _isWithdrawAgreementChecked.value = isWithdrawAgreementChecked.value?.not()
        }

        fun withdraw() {
            viewModelScope.launch {
                runCatching {
                    val withdrawReason = when (withdrawReason.value) {
                        ETC_INPUT_REASON -> withdrawEtcReason.value
                        else -> withdrawReason.value
                    } ?: ""
                    authRepository.withdraw(withdrawReason)
                }.onSuccess {
                    _isWithDrawSuccess.value = true
                    authRepository.updateIsAutoLogin(false)
                    pushMessageRepository.clearFCMToken()
                }.onFailure {
                    _isWithDrawSuccess.value = false
                }
            }
        }

        companion object {
            private const val ETC_INPUT_REASON = "직접입력"
        }
    }
