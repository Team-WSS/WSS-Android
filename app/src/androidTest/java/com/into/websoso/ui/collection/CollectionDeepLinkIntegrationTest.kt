package com.into.websoso.ui.collection

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.into.websoso.BuildConfig
import com.into.websoso.ui.login.LoginActivity
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.onboarding.OnboardingActivity
import com.into.websoso.ui.onboarding.welcome.WelcomeActivity
import com.into.websoso.ui.splash.SplashActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollectionDeepLinkIntegrationTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun kakaoCollectionLinkResolvesToExportedSplash() {
        val link = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("kakao${BuildConfig.KAKAO_APP_KEY}://kakaolink?collectionId=123"),
        ).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            setPackage(context.packageName)
        }
        val activity = context.packageManager.resolveActivity(link, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo

        assertEquals(SplashActivity::class.java.name, activity?.name)
        assertTrue(activity?.exported == true)
        assertEquals(
            123L,
            CollectionDeepLink.parseCollectionId(link.dataString, "kakao${BuildConfig.KAKAO_APP_KEY}"),
        )
    }

    @Test
    fun collectionIdSurvivesLoginAndOnboardingIntents() {
        var source = Intent().putExtra(CollectionDeepLink.PENDING_COLLECTION_ID, 123L)
        listOf(
            LoginActivity.getIntent(context),
            OnboardingActivity.getIntent(context),
            WelcomeActivity.getIntent(context, "테스트"),
            MainActivity.getIntent(context, true),
        ).forEach { destination ->
            source = CollectionDeepLink.forward(source, destination)
            assertEquals(123L, source.getLongExtra(CollectionDeepLink.PENDING_COLLECTION_ID, 0L))
        }
    }
}
