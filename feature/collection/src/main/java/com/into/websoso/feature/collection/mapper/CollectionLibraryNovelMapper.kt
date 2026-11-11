package com.into.websoso.feature.collection.mapper

import com.into.websoso.core.common.extensions.formatDateRange
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.feature.collection.model.CollectionLibraryNovelUiModel
import com.into.websoso.feature.collection.model.CollectionLibraryRatingStar
import com.into.websoso.feature.collection.model.CollectionLibraryReadStatus

internal fun NovelEntity.toUiModel(): CollectionLibraryNovelUiModel =
    CollectionLibraryNovelUiModel(
        novelId = novelId,
        title = title,
        imageUrl = novelImage,
        readStatus = CollectionLibraryReadStatus.entries.find { it.name == readStatus },
        isInterested = isInterest,
        ratingStars = userNovelRating.toRatingStars(),
        dateText = formatDateRange(startDate, endDate),
    )

private fun Float.toRatingStars(): List<CollectionLibraryRatingStar> {
    if (this !in 0.5f..5f) return emptyList()

    val fullStarCount = toInt()
    val hasHalfStar = this - fullStarCount >= 0.5f
    val emptyStarCount = 5 - fullStarCount - if (hasHalfStar) 1 else 0

    return buildList {
        repeat(fullStarCount) { add(CollectionLibraryRatingStar.FULL) }
        if (hasHalfStar) add(CollectionLibraryRatingStar.HALF)
        repeat(emptyStarCount) { add(CollectionLibraryRatingStar.EMPTY) }
    }
}
