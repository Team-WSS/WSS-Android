package com.into.websoso.domain.mapper

import com.into.websoso.data.model.ExploreResultEntity
import com.into.websoso.data.model.ExploreResultEntity.NovelEntity
import com.into.websoso.domain.model.ExploreResult
import com.into.websoso.domain.model.ExploreResult.Novel

fun ExploreResultEntity.toDomain(): ExploreResult {
    return ExploreResult(
        resultCount = resultCount,
        isLoadable = isLoadable,
        novels = novels.map { it.toDomain() },
    )
}

fun NovelEntity.toDomain(): Novel {
    return Novel(
        id = id,
        title = title,
        author = author,
        image = image,
        interestedCount = interestedCount,
        rating = rating,
        ratingCount = ratingCount,
    )
}