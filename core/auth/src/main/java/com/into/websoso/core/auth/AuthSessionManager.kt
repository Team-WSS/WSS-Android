package com.into.websoso.core.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthSessionManager {
    val sessionState: StateFlow<SessionState>

    fun clearSessionState()

    suspend fun updateSessionState(sessionState: SessionState)
}

sealed interface SessionState {
    data object Expired : SessionState

    data object Idle : SessionState
}
