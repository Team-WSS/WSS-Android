package com.into.websoso.ui.termsAgreement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.ui.termsAgreement.model.AgreementType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TermsAgreementViewModel @Inject constructor() : ViewModel() {

    private val _agreementStatus = MutableStateFlow(
        mapOf(
            AgreementType.SERVICE to false,
            AgreementType.PRIVACY to false,
            AgreementType.MARKETING to false,
        ),
    )
    val agreementStatus: StateFlow<Map<AgreementType, Boolean>> = _agreementStatus.asStateFlow()

    val isAllChecked: StateFlow<Boolean> = agreementStatus
        .map { it.values.all { checked -> checked } }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isRequiredAgreementsChecked: StateFlow<Boolean> = agreementStatus
        .map { isRequiredAgreementChecked(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private fun isRequiredAgreementChecked(status: Map<AgreementType, Boolean>): Boolean {
        return status[AgreementType.SERVICE] == true && status[AgreementType.PRIVACY] == true
    }

    fun updateTermsAgreementsAll() {
        val newStatus = agreementStatus.value.values.any { !it }
        _agreementStatus.update { status -> status.mapValues { newStatus } }
    }

    fun updateTermsAgreements(agreementType: AgreementType) {
        _agreementStatus.value[agreementType]?.let { currentValue ->
            _agreementStatus.value = _agreementStatus.value.toMutableMap().apply {
                this[agreementType] = !currentValue
            }
        }
    }
}
