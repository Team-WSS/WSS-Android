package com.teamwss.websoso.data.authenticator

import android.content.Context
import android.content.Intent
import com.kakao.sdk.user.UserApiClient
import com.teamwss.websoso.data.remote.api.AuthApi
import com.teamwss.websoso.data.repository.AuthRepository
import com.teamwss.websoso.ui.login.LoginActivity
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
            val refreshToken = runBlocking { authRepository.fetchRefreshToken() }
            if (refreshToken.isEmpty()) {
                return null
            }

            val newAccessToken = runCatching {
                runBlocking {
                    authRepository.reissueToken(refreshToken)
                }
            }.getOrElse {
                handleLogout()
                return null
            }

            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
        return null
    }

    private fun handleLogout() {
        runBlocking {
            authRepository.clearTokens()
            UserApiClient.instance.logout {
                val loginIntent = Intent(context, LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(loginIntent)
            }
        }
    }
}
