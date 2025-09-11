package com.into.websoso.ui.login

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.into.websoso.core.auth.AuthClient
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.util.setupSystemBarIconColor
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.signin.SignInScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    // TODO: CompositionLocal로 주입
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    @Inject
    lateinit var authClient: Map<AuthPlatform, @JvmSuppressWildcards AuthClient>

    @Inject
    lateinit var websosoNavigator: NavigatorProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSystemBarIconColor(true)
        setContent {
            WebsosoTheme {
                SignInScreen(
                    authClient = { platform -> authClient[platform] },
                    websosoNavigator = websosoNavigator,
                )
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, LoginActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }
    }
}
