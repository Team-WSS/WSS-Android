package com.into.websoso.data.authenticator

import android.content.Context
import com.kakao.sdk.user.UserApiClient
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.ui.login.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebsosoAuthenticator @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") == null) {
            return null
        }

        if (response.code == 401) {
            if (authRepository.refreshToken.isBlank()) {
                return null
            }

            val newAccessToken = runCatching {
                runBlocking {
                    authRepository.reissueToken()
                }
            }.onFailure {
                runBlocking {
                    authRepository.clearTokens()
                    UserApiClient.instance.logout {
                        context.startActivity(LoginActivity.getIntent(context))
                    }
                }
            }.getOrThrow()

            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
        return null
    }
}
