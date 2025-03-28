package com.into.websoso.data.remote.api

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.into.websoso.data.mapper.toOAuthToken
import com.into.websoso.data.model.OAuthToken
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoAuthService @Inject constructor(
    @ActivityContext private val context: Context,
    private val client: UserApiClient,
) : OAuthService {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private val isKakaoTalkLoginAvailable: Boolean
        get() = client.isKakaoTalkLoginAvailable(context)

    override suspend fun login(): OAuthToken = suspendCancellableCoroutine {
        if (isKakaoTalkLoginAvailable) {
            client.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // Firebase Analytics에 실패 이벤트 로그 기록
                    firebaseAnalytics.logEvent("kakao_login_failure") {
                        param("error_message", error.message ?: "Unknown Error")
                        param("login_method", "kakao_talk")
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    client.loginWithKakaoAccount(context) { accountToken, accountError ->
                        if (accountError != null) {
                            it.resumeWithException(accountError)
                        } else if (accountToken != null) {
                            it.resume(accountToken.toOAuthToken())
                        }
                    }
                } else if (token != null) {
                    it.resume(token.toOAuthToken())
                }
            }
        } else {
            client.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    it.resumeWithException(error)
                } else if (token != null) {
                    it.resume(token.toOAuthToken())
                }
            }
        }
    }

    override suspend fun logout() = suspendCancellableCoroutine {
        client.logout { error ->
            if (error != null) {
                it.resumeWithException(error)
            } else {
                it.resume(Unit)
            }
        }
    }

    override suspend fun withdraw() = suspendCancellableCoroutine {
        client.unlink { error ->
            if (error != null) {
                it.resumeWithException(error)
            } else {
                it.resume(Unit)
            }
        }
    }
}
