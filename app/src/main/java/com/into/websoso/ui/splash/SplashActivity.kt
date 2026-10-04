package com.into.websoso.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure.ANDROID_ID
import android.provider.Settings.Secure.getString
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.into.websoso.BuildConfig
import com.into.websoso.R
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.databinding.ActivitySplashBinding
import com.into.websoso.ui.collection.CollectionDeepLink
import com.into.websoso.ui.main.home.HomeStartupRequest
import com.into.websoso.ui.splash.UiEffect.NavigateToLogin
import com.into.websoso.ui.splash.UiEffect.NavigateToMain
import com.into.websoso.ui.splash.UiEffect.ShowDialog
import com.into.websoso.ui.splash.dialog.MinimumVersionDialogFragment
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    @Inject
    lateinit var websosoNavigator: NavigatorProvider

    @Inject
    lateinit var homeStartupRequest: Lazy<HomeStartupRequest>

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareCollectionDeepLink()

        updateUserDeviceIdentifier()
        collectUiEffect()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        prepareCollectionDeepLink()
    }

    private fun prepareCollectionDeepLink() {
        intent.removeExtra(CollectionDeepLink.PENDING_COLLECTION_ID)
        if (intent.action == Intent.ACTION_VIEW) {
            val collectionId = CollectionDeepLink.parseCollectionId(
                link = intent.dataString,
                expectedScheme = "kakao${BuildConfig.KAKAO_APP_KEY}",
            )
            if (collectionId != null) {
                intent.putExtra(CollectionDeepLink.PENDING_COLLECTION_ID, collectionId)
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun updateUserDeviceIdentifier() {
        val deviceId = getString(contentResolver, ANDROID_ID).orEmpty()
        splashViewModel.updateUserDeviceIdentifier(deviceIdentifier = deviceId)
    }

    private fun collectUiEffect() {
        splashViewModel.uiEffect.collectWithLifecycle(this) { uiEffect ->
            when (uiEffect) {
                NavigateToLogin -> websosoNavigator.navigateToLoginActivity(::startDestination)
                NavigateToMain -> websosoNavigator.navigateToMainActivity(::startHomeDestination)
                ShowDialog -> showMinimumVersionDialog()
            }
        }
    }

    private fun startHomeDestination(destination: Intent) {
        lifecycleScope.launch {
            val target = CollectionDeepLink.forward(intent, destination)
            var requestId: String? = null
            var handedOff = false
            try {
                if (target.getLongExtra(CollectionDeepLink.PENDING_COLLECTION_ID, 0L) == 0L) {
                    requestId = homeStartupRequest.get().start()
                    requestId?.let { target.putExtra(HomeStartupRequest.KEY, it) }
                }
                // Authentication has completed. Start Home without awaiting either content response.
                startActivity(target)
                handedOff = true
                finish()
            } finally {
                if (!handedOff && requestId != null) homeStartupRequest.get().discard(requestId)
            }
        }
    }

    private fun startDestination(destination: Intent) {
        startActivity(CollectionDeepLink.forward(intent, destination))
        finish()
    }

    private fun showMinimumVersionDialog() {
        val dialog = MinimumVersionDialogFragment.newInstance()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, MinimumVersionDialogFragment.MINIMUM_VERSION_TAG)
    }
}
