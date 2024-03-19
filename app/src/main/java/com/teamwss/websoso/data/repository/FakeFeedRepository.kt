package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.FeedEntity

class FakeFeedRepository {

    val dummyFeedData = listOf(
        FeedEntity(
            id = 1,
            user = FeedEntity.UserInfo(
                id = 101,
                name = "John Doe",
                profileImage = "https://example.com/profiles/johndoe.jpg"
            ),
            content = "Exciting new chapter released today!",
            genre = listOf("Fantasy", "Adventure"),
            novelInfo = FeedEntity.NovelInfo(
                id = 201,
                name = "The Chronicles of Adventure",
                score = 4.5,
                count = 100
            ),
            likeCount = 25,
            commentCount = 10
        ),
        FeedEntity(
            id = 2,
            user = FeedEntity.UserInfo(
                id = 102,
                name = "Jane Smith",
                profileImage = "https://example.com/profiles/janesmith.jpg"
            ),
            content = "What do you think about the latest plot twist?",
            genre = listOf("Mystery", "Thriller"),
            novelInfo = FeedEntity.NovelInfo(
                id = 202,
                name = "The Enigma of Secrets",
                score = 4.2,
                count = 85
            ),
            likeCount = 30,
            commentCount = 15
        ),
        FeedEntity(
            id = 3,
            user = FeedEntity.UserInfo(
                id = 103,
                name = "Alice Johnson",
                profileImage = "https://example.com/profiles/alicejohnson.jpg"
            ),
            content = "Reached a milestone of 100 chapters today!",
            genre = listOf("Romance", "Drama"),
            novelInfo = FeedEntity.NovelInfo(
                id = 203,
                name = "Love in the Moonlight",
                score = 4.8,
                count = 150
            ),
            likeCount = 50,
            commentCount = 20
        )
    )

}