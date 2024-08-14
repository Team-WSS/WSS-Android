package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.MyActivitiesEntity

class MyActivityRepository {

    fun getMyActivities(): List<MyActivitiesEntity.MyActivityEntity> {
        return listOf(
            MyActivitiesEntity.MyActivityEntity(
                feedId = 1,
                userId = 101,
                profileImg = "https://example.com/profile1.jpg",
                nickname = "John Doe",
                isSpoiler = false,
                feedContent = "This is a great novel about adventure and magic. Highly recommended!",
                createdDate = "2023-08-14",
                isModified = false,
                isLiked = true,
                likeCount = 120,
                commentCount = 45,
                novelId = 1001,
                title = "Magic Adventure",
                novelRatingCount = 500,
                novelRating = 4.5,
                relevantCategories = listOf("Fantasy", "Adventure")
            ),
            MyActivitiesEntity.MyActivityEntity(
                feedId = 2,
                userId = 102,
                profileImg = "https://example.com/profile2.jpg",
                nickname = "Jane Smith",
                isSpoiler = true,
                feedContent = "The ending was totally unexpected, and it made me cry!",
                createdDate = "2023-08-13",
                isModified = true,
                isLiked = false,
                likeCount = 30,
                commentCount = 12,
                novelId = 1002,
                title = "Unexpected Journey",
                novelRatingCount = 250,
                novelRating = 3.9,
                relevantCategories = listOf("Drama", "Romance")
            ),
            MyActivitiesEntity.MyActivityEntity(
                feedId = 3,
                userId = 103,
                profileImg = "https://example.com/profile3.jpg",
                nickname = "Alice Johnson",
                isSpoiler = false,
                feedContent = "A well-written novel with deep characters and a complex plot.",
                createdDate = "2023-08-12",
                isModified = false,
                isLiked = true,
                likeCount = 75,
                commentCount = 20,
                novelId = 1003,
                title = "Deep Shadows",
                novelRatingCount = 300,
                novelRating = 4.0,
                relevantCategories = listOf("Mystery", "Thriller")
            ),
            MyActivitiesEntity.MyActivityEntity(
                feedId = 4,
                userId = 104,
                profileImg = "https://example.com/profile4.jpg",
                nickname = "Bob Lee",
                isSpoiler = false,
                feedContent = "An average story with some good moments, but overall not that impressive.",
                createdDate = "2023-08-11",
                isModified = false,
                isLiked = false,
                likeCount = 15,
                commentCount = 5,
                novelId = 1004,
                title = "Mediocre Tales",
                novelRatingCount = 100,
                novelRating = 2.5,
                relevantCategories = listOf("Drama")
            ),
            MyActivitiesEntity.MyActivityEntity(
                feedId = 5,
                userId = 105,
                profileImg = "https://example.com/profile5.jpg",
                nickname = "Charlie Brown",
                isSpoiler = true,
                feedContent = "Spoiler alert! The protagonist loses everything at the end.",
                createdDate = "2023-08-10",
                isModified = true,
                isLiked = true,
                likeCount = 200,
                commentCount = 60,
                novelId = 1005,
                title = "Tragic Hero",
                novelRatingCount = 800,
                novelRating = 4.8,
                relevantCategories = listOf("Tragedy", "Epic")
            )
        )
    }
}
