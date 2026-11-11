package com.into.websoso.ui.collection

import com.into.websoso.feature.collection.model.CollectionShareContent

internal object CollectionKakaoShare {
    // 카카오 디벨로퍼스 콘솔의 메시지 템플릿(카카오링크)과 1:1로 연결된 ID.
    // 콘솔에서 템플릿을 재생성하면 값이 바뀌므로 함께 갱신해야 한다.
    private const val TEMPLATE_ID_ONE_COVER = 136782L
    private const val TEMPLATE_ID_TWO_COVERS = 136783L
    private const val TEMPLATE_ID_THREE_COVERS = 136784L

    fun templateId(content: CollectionShareContent): Long =
        when (content.imageUrls.size) {
            1 -> TEMPLATE_ID_ONE_COVER
            2 -> TEMPLATE_ID_TWO_COVERS
            3 -> TEMPLATE_ID_THREE_COVERS
            else -> error("Collection sharing requires 1 to 3 covers")
        }

    fun templateArgs(content: CollectionShareContent): Map<String, String> =
        buildMap {
            put("collectionId", content.collectionId.toString())
            put("TITLE", content.title)
            put("NICKNAME", content.nickname)
            content.imageUrls.forEachIndexed { index, url -> put("IMAGE${index + 1}", url) }
        }
}
