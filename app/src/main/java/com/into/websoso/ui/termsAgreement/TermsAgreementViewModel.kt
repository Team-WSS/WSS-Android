package com.into.websoso.ui.termsAgreement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.remote.request.TermsAgreementRequestDto
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.termsAgreement.model.AgreementType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermsAgreementViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _agreementStatus = MutableStateFlow(
            mapOf(
                AgreementType.SERVICE to false,
                AgreementType.PRIVACY to false,
                AgreementType.MARKETING to false,
            ),
        )
        val agreementStatus: StateFlow<Map<AgreementType, Boolean>> = _agreementStatus.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _saveAgreementResult = MutableStateFlow<Result<Unit>?>(null)
        val saveAgreementResult: StateFlow<Result<Unit>?> = _saveAgreementResult.asStateFlow()

        val isRequiredAgreementsChecked: StateFlow<Boolean> = agreementStatus
            .map { isRequiredAgreementChecked(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

        private fun isRequiredAgreementChecked(status: Map<AgreementType, Boolean>): Boolean =
            status[AgreementType.SERVICE] == true && status[AgreementType.PRIVACY] == true

        fun updateTermsAgreementsAll() {
            val newStatus = _agreementStatus.value.values.any { !it }
            _agreementStatus.update { it.mapValues { _ -> newStatus } }
        }

        fun updateTermsAgreements(agreementType: AgreementType) {
            _agreementStatus.value[agreementType]?.let { currentValue ->
                _agreementStatus.value = _agreementStatus.value.toMutableMap().apply {
                    this[agreementType] = !currentValue
                }
            }
        }

        fun saveTermsAgreements() {
            if (!isRequiredAgreementsChecked.value) return

            viewModelScope.launch {
                _isLoading.value = true

                val agreementRequest = TermsAgreementRequestDto(
                    serviceAgreed = _agreementStatus.value[AgreementType.SERVICE] == true,
                    privacyAgreed = _agreementStatus.value[AgreementType.PRIVACY] == true,
                    marketingAgreed = _agreementStatus.value[AgreementType.MARKETING] == true,
                )

                _saveAgreementResult.value = runCatching {
                    userRepository.saveTermsAgreements(
                        agreementRequest.serviceAgreed,
                        agreementRequest.privacyAgreed,
                        agreementRequest.marketingAgreed,
                    )
                }

                _isLoading.value = false
            }
        }
    }
