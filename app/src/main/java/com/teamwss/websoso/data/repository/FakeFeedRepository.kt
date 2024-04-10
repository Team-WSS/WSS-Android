package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity

class FakeFeedRepository {

    val dummyData: FeedsEntity = FeedsEntity(
        "Default", listOf(
            FeedEntity(
                user = FeedEntity.UserEntity(
                    id = 1,
                    nickname = "JohnDoe",
                    profileImage = "avatar1.jpg",
                ),
                createdDate = "2024-03-29",
                id = 1,
                content = "This is the first feed content.",
                relevantCategories = listOf("Fantasy", "Adventure"),
                likeCount = "10",
                likeUsers = listOf(101, 102, 103),
                commentCount = "3",
                isModified = false,
                isSpoiled = false,
                novel = FeedEntity.NovelEntity(
                    id = 1,
                    title = "The Lorem Ipsum Chronicles",
                    rating = 4.5,
                    ratingCount = 200
                )
            ),
            FeedEntity(
                user = FeedEntity.UserEntity(
                    id = 2,
                    nickname = "JaneSmith",
                    profileImage = "avatar2.jpg",
                ),
                createdDate = "2024-03-28",
                id = 2,
                content = "This is the second feed content.",
                relevantCategories = listOf("Sci-Fi", "Thriller"),
                likeCount = "5",
                likeUsers = listOf(104, 105),
                commentCount = "2",
                isModified = false,
                isSpoiled = true,
                novel = FeedEntity.NovelEntity(
                    id = 2,
                    title = "Science Fiction Tales",
                    rating = 3.8,
                    ratingCount = 150,
                )
            )
        )
    )
}
