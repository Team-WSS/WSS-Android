package com.into.websoso.ui.feedDetail.model

enum class ImageContainerType(
    private val size: Int,
) {
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3),
    MULTIPLE(4),
    ;

    companion object {
        fun from(size: Int): ImageContainerType = ImageContainerType.entries.find { it.size == size } ?: MULTIPLE
    }
}
