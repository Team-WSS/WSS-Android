package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.data.model.ExploreResultEntity.NovelEntity
import com.teamwss.websoso.domain.model.ExploreResult
import com.teamwss.websoso.domain.model.ExploreResult.Novel

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