package com.into.websoso.core.auth

import kotlinx.coroutines.flow.SharedFlow

interface AuthSessionManager {
    val sessionExpired: SharedFlow<Unit>

    suspend fun onSessionExpired()
}
