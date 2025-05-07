package com.into.websoso.core.auth_kakao

import android.content.Context
import com.into.websoso.core.auth.AuthClient
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.core.auth.toAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoAuthClient
    @Inject
    constructor(
        @ActivityContext private val context: Context,
    ) : AuthClient {
        private val client: UserApiClient by lazy { UserApiClient.instance }
        private val isKakaoTalkLoginAvailable: Boolean
            get() = client.isKakaoTalkLoginAvailable(context)

        override suspend fun signIn(): AuthToken =
            suspendCancellableCoroutine { loginContinuation ->
                if (isKakaoTalkLoginAvailable) {
                    loginWithKakaotalk(loginContinuation)
                } else {
                    loginWithKakaoAccount(loginContinuation)
                }
            }

        override suspend fun signOut() {
            suspendCancellableCoroutine {
                client.logout { error ->
                    if (error != null) {
                        it.resumeWithException(error)
                    } else {
                        it.resume(Unit)
                    }
                }
            }
        }

        override suspend fun withdraw() {
            suspendCancellableCoroutine {
                client.unlink { error ->
                    if (error != null) {
                        it.resumeWithException(error)
                    } else {
                        it.resume(Unit)
                    }
                }
            }
        }

        private fun loginWithKakaotalk(loginContinuation: CancellableContinuation<AuthToken>) {
            client.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    loginWithKakaoAccount(loginContinuation)
                } else if (token != null) {
                    loginContinuation.resume(token.accessToken.toAuthToken())
                }
            }
        }

        private fun loginWithKakaoAccount(loginContinuation: CancellableContinuation<AuthToken>) {
            client.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    loginContinuation.resumeWithException(error)
                } else if (token != null) {
                    loginContinuation.resume(token.accessToken.toAuthToken())
                }
            }
        }
    }
