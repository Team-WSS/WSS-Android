package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.domain.model.Feed

object FeedMapper {

    fun FeedEntity.toDomain(): Feed =
        Feed(
            id = id,
            user = Feed.UserInfo(
                id = user.id,
                name = user.name,
                profileImage = user.profileImage
            ),
            content = content,
            genre = genre,
            novelInfo = Feed.NovelInfo(
                id = novelInfo.id,
                name = novelInfo.name,
                score = novelInfo.score,
                count = novelInfo.count,
            ),
            likeCount = likeCount,
            commentCount = commentCount,
        )
}