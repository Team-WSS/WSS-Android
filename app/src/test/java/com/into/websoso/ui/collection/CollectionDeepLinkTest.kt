package com.into.websoso.ui.collection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CollectionDeepLinkTest {
    @Test
    fun `read a positive collection ID from the expected Kakao link`() {
        assertEquals(123L, parse("kakaotest://kakaolink?collectionId=123"))
        assertEquals(123L, parse("kakaotest://kakaolink/?collectionId=123&source=share"))
        assertEquals(123L, parse("kakaotest://kakaolink?collectionId=%31%32%33"))
        assertEquals(Long.MAX_VALUE, parse("kakaotest://kakaolink?collectionId=${Long.MAX_VALUE}"))
    }

    @Test
    fun `reject malformed IDs and ambiguous duplicate parameters`() {
        listOf("", "0", "-1", "+1", "1.5", "abc", "9223372036854775808", "%20", "%ZZ").forEach {
            assertNull(parse("kakaotest://kakaolink?collectionId=$it"))
        }
        assertNull(parse("kakaotest://kakaolink?collectionId=1&collectionId=2"))
        assertNull(parse("kakaotest://kakaolink?collectionId=1&collection%49d=2"))
        assertNull(parse("kakaotest://kakaolink?collectionId"))
        assertNull(parse("kakaotest://kakaolink?otherId=1"))
    }

    @Test
    fun `reject unrelated routes and invalid URLs`() {
        listOf(
            null,
            "",
            "not a URL",
            "https://kakaolink?collectionId=1",
            "kakaoother://kakaolink?collectionId=1",
            "kakaotest://oauth?collectionId=1",
            "kakaotest://kakaolink.evil?collectionId=1",
            "kakaotest://user@kakaolink?collectionId=1",
            "kakaotest://kakaolink:80?collectionId=1",
            "kakaotest://kakaolink/other?collectionId=1",
            "kakaotest://kakaolink?collectionId=1#fragment",
        ).forEach { assertNull(parse(it)) }
    }

    private fun parse(link: String?): Long? = CollectionDeepLink.parseCollectionId(link, "kakaotest")
}
