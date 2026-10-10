package com.into.websoso.feature.collection

import com.into.websoso.domain.collection.model.CollectionDetail
import com.into.websoso.domain.collection.model.CollectionNovel
import com.into.websoso.domain.collection.model.CollectionOwner
import com.into.websoso.feature.collection.model.CollectionShareContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CollectionShareContentTest {
    @Test
    fun `대표 표지를 먼저 배치하고 최대 세 작품까지 공유하되 원본 순서는 유지한다`() {
        (1..4).forEach { count ->
            val collection = collection(count)
            val content = requireNotNull(CollectionShareContent.from(collection))
            val expectedIds = (listOf(count) + (1 until count)).take(3)
            assertEquals(expectedIds.map { "https://example.com/$it.jpg" }, content.imageUrls)
            assertEquals((1..count).map(Int::toLong), collection.novels.map { it.id })
            assertEquals(collection.id, content.collectionId)
            assertEquals(collection.name, content.title)
            assertEquals(collection.owner.nickname, content.nickname)
        }
    }

    @Test
    fun `비공개 컬렉션과 불완전한 공유 정보는 공유하지 않는다`() {
        val collection = collection(2)
        assertNull(CollectionShareContent.from(collection.copy(isPublic = false)))
        assertNull(CollectionShareContent.from(collection.copy(id = 0)))
        assertNull(CollectionShareContent.from(collection.copy(novelCount = 0)))
        assertNull(CollectionShareContent.from(collection.copy(novels = emptyList())))
        assertNull(CollectionShareContent.from(collection.copy(representativeNovelId = 99)))
        assertNull(CollectionShareContent.from(collection.copy(novelCount = 3)))
        assertNull(CollectionShareContent.from(collection.copy(novels = collection.novels.map { it.copy(imageUrl = " ") })))
    }

    private fun collection(count: Int) =
        CollectionDetail(
            id = 123L,
            name = "추천 컬렉션",
            description = null,
            isPublic = true,
            isMine = false,
            owner = CollectionOwner(10L, "컬렉션 작성자", ""),
            representativeNovelId = count.toLong(),
            novelCount = count,
            likeCount = 0,
            isLiked = false,
            novels = (1..count).map { CollectionNovel(it.toLong(), "작품 $it", "https://example.com/$it.jpg", "작가") },
        )
}
