package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TermsAgreementResponseDto(
    @SerialName("serviceAgreed")
    val serviceAgreed: Boolean,
    @SerialName("privacyAgreed")
    val privacyAgreed: Boolean,
    @SerialName("marketingAgreed")
    val marketingAgreed: Boolean,
)
