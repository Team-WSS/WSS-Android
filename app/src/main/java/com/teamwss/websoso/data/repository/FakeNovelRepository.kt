package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.NormalExploreEntity

class FakeNovelRepository {
    val normalExploreResultDummyData: NormalExploreEntity = NormalExploreEntity(
        resultCount = 8,
        isLoadable = false,
        novels = List(9) {
            NormalExploreEntity.Novel(
                id = (it + 1).toLong(),
                title = "악역의 엔딩은 죽음뿐이다 두두둥둥",
                author = "손명지${it + 1}",
                image = "https://comicthumb-phinf.pstatic.net/20200414_278/pocket_1586837579683L65DB_JPEG/--_%28720x1098%29.jpg?type=m260",
                interestedCount = (it + 1).toLong(),
                rating = 4.0f,
                ratingCount = 100 + (it + 1).toLong(),
            )
        }
    )
}