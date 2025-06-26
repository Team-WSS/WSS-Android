package com.into.websoso.ui.feedDetail.model

enum class ImageContainerType(
    val size: Int,
) {
    EMPTY(0),
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3),
    MULTIPLE(4),
    ;

    companion object {
        fun from(size: Int): ImageContainerType = ImageContainerType.entries.find { it.size == size } ?: MULTIPLE
    }
}
