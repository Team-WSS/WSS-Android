package com.into.websoso.ui.collection

import com.into.websoso.feature.collection.model.CollectionShareContent
import org.junit.Assert.assertEquals
import org.junit.Test

class CollectionKakaoShareTest {
    @Test
    fun `표지 수에 맞는 템플릿과 사용자 인자를 전달한다`() {
        listOf(136782L, 136783L, 136784L).forEachIndexed { index, templateId ->
            val images = (1..index + 1).map { "https://example.com/$it.jpg" }
            val content = CollectionShareContent(123L, "추천 컬렉션", "작성자", images)
            assertEquals(templateId, CollectionKakaoShare.templateId(content))
            assertEquals(
                mapOf("collectionId" to "123", "TITLE" to "추천 컬렉션", "NICKNAME" to "작성자") +
                    images.mapIndexed { imageIndex, url -> "IMAGE${imageIndex + 1}" to url },
                CollectionKakaoShare.templateArgs(content),
            )
        }
    }
}
