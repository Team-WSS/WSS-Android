package com.into.websoso.data.account

fun interface AccountTokenProvider {
    suspend operator fun invoke()
}
