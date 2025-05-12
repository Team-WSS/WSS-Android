package com.into.websoso.core.common.util.sessionManager

import com.into.websoso.core.auth.AuthSessionManager
import com.into.websoso.core.auth.SessionState
import com.into.websoso.core.auth.SessionState.Idle
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

internal class WebsosoAuthSessionManager
    @Inject
    constructor() : AuthSessionManager {
        private val _sessionState = MutableStateFlow<SessionState>(Idle)
        override val sessionState: StateFlow<SessionState> get() = _sessionState.asStateFlow()

        override fun clearSessionState() {
            _sessionState.update { Idle }
        }

        override suspend fun updateSessionState(sessionState: SessionState) {
            _sessionState.update { sessionState }
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface WebsosoAuthSessionManagerModule {
    @Binds
    @Singleton
    fun bindWebsosoAuthSessionManager(websosoAuthSessionManager: WebsosoAuthSessionManager): AuthSessionManager
}
