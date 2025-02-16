package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TermsAgreementRequestDto(
    @SerialName("serviceAgreed")
    val serviceAgreed: Boolean,
    @SerialName("privacyAgreed")
    val privacyAgreed: Boolean,
    @SerialName("marketingAgreed")
    val marketingAgreed: Boolean,
)
