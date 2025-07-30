package com.into.websoso.core.common.util.sessionManager

import com.into.websoso.core.auth.AuthSessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WebsosoAuthSessionManager
    @Inject
    constructor() : AuthSessionManager {
        private val _sessionExpired = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)
        override val sessionExpired: SharedFlow<Unit> get() = _sessionExpired.asSharedFlow()

        override suspend fun onSessionExpired() {
            _sessionExpired.emit(Unit)
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface WebsosoAuthSessionManagerModule {
    @Binds
    fun bindWebsosoAuthSessionManager(websosoAuthSessionManager: WebsosoAuthSessionManager): AuthSessionManager
}
