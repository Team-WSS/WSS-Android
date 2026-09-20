package com.into.websoso.ui.collection

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.into.websoso.feature.collection.model.CollectionShareContent
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.KakaoJson
import com.kakao.sdk.network.ApiFactory
import com.kakao.sdk.share.ShareApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollectionKakaoShareIntegrationTest {
    @Test
    fun registeredTemplatesAcceptDebugAppRequests() {
        // Opt in explicitly: this checks the live Kakao configuration without sending messages.
        assumeTrue(InstrumentationRegistry.getArguments().getString("verifyKakaoTemplates") == "true")
        val loggingEnabled = KakaoSdk.loggingEnabled
        KakaoSdk.loggingEnabled = false
        try {
            val api = ApiFactory.kapi.create(ShareApi::class.java)
            (1..3).forEach { coverCount ->
                val content = CollectionShareContent(
                    collectionId = 123L,
                    title = "공유 설정 확인",
                    nickname = "테스트",
                    // Public sample image from Kakao's sharing documentation.
                    imageUrls = List(coverCount) {
                        "https://mud-kage.kakao.com/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg"
                    },
                )
                val templateId = CollectionKakaoShare.templateId(content)
                val response = try {
                    api.validateCustom(
                        templateId,
                        KakaoJson.encodeToJsonObject(CollectionKakaoShare.templateArgs(content)),
                    ).execute()
                } catch (error: Exception) {
                    throw AssertionError("Template $templateId: ${error.javaClass.simpleName}")
                }
                assertTrue("Template $templateId: HTTP ${response.code()}", response.isSuccessful)
                val result = response.body()
                assertNotNull("Template $templateId: empty response", result)
                assertEquals(templateId, result?.templateId)
                assertTrue(
                    "Template $templateId: template warnings ${result?.warningMsg?.keys}",
                    result?.warningMsg.isNullOrEmpty(),
                )
                assertTrue("Template $templateId: argument warnings", result?.argumentMsg.isNullOrEmpty())
            }
        } finally {
            KakaoSdk.loggingEnabled = loggingEnabled
        }
    }
}
