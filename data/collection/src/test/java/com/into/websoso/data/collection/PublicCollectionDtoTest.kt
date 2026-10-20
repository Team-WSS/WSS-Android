package com.into.websoso.data.collection

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PublicCollectionDtoTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `공개 컬렉션 목록 응답을 작성자와 표지까지 도메인으로 변환한다`() {
        val page = json.decodeFromString<PublicCollectionPageResponseDto>(FIRST_PAGE).toCursorPage()

        assertTrue(page.hasNext)
        assertEquals("cursor-1", page.nextCursor)
        val first = page.items.first()
        assertEquals(31L, first.id)
        assertEquals("취향 저격 로판", first.name)
        assertEquals(24, first.novelCount)
        assertEquals(7L, first.owner.id)
        assertEquals("웹소소", first.owner.nickname)
        assertEquals(listOf(9L, 5L), first.recentNovels.map { it.id })
    }

    @Test
    fun `설명이 null이고 작품이 하나뿐인 컬렉션도 변환한다`() {
        val page = json.decodeFromString<PublicCollectionPageResponseDto>(FIRST_PAGE).toCursorPage()

        val second = page.items[1]
        assertNull(second.description)
        assertEquals(1, second.recentNovels.size)
    }

    @Test
    fun `마지막 페이지는 nextCursor 없이 내려온다`() {
        val page = json
            .decodeFromString<PublicCollectionPageResponseDto>("""{"hasNext":false,"nextCursor":null,"collections":[]}""")
            .toCursorPage()

        assertFalse(page.hasNext)
        assertNull(page.nextCursor)
        assertTrue(page.items.isEmpty())
    }

    private companion object {
        const val FIRST_PAGE = """
            {
              "hasNext": true,
              "nextCursor": "cursor-1",
              "collections": [
                {
                  "collectionId": 31,
                  "collectionName": "취향 저격 로판",
                  "collectionDescription": "여주가 강한 로맨스 판타지 모음",
                  "novelCount": 24,
                  "owner": { "userId": 7, "nickname": "웹소소", "avatarImage": "https://image.websoso/avatar/1.png" },
                  "recentNovels": [
                    { "novelId": 9, "title": "전지적 독자 시점", "novelImage": "https://image.websoso/novel/9.png", "author": "싱숑" },
                    { "novelId": 5, "title": "재혼 황후", "novelImage": "https://image.websoso/novel/5.png", "author": "알파타르트" }
                  ]
                },
                {
                  "collectionId": 12,
                  "collectionName": "추천 소설",
                  "collectionDescription": null,
                  "novelCount": 1,
                  "owner": { "userId": 45, "nickname": "콜렉터", "avatarImage": "https://image.websoso/avatar/2.png" },
                  "recentNovels": [
                    { "novelId": 1001, "title": "작품 제목", "novelImage": "https://image.websoso/novel/1001.png", "author": "작가명" }
                  ]
                }
              ]
            }
        """
    }
}
