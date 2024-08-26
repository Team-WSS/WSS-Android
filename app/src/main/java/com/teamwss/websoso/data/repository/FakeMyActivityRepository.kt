package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.MyActivitiesEntity
import javax.inject.Inject

class MyActivityRepository @Inject constructor() {

    fun getMyActivities(): List<MyActivitiesEntity.MyActivityEntity> {
        return listOf(
            MyActivitiesEntity.MyActivityEntity(
                feedId = 1,
                userId = 101,
                profileImg = "https://github.com/user-attachments/assets/d4fa43d8-d90a-4ad9-bfa9-cc12fda05e1d",
                nickname = "샐리",
                isSpoiler = false,
                feedContent = "판소추천해요! 완결난지는 좀 되었는데 추천합니다. scp 같은 이상현상 물품을 모아놓은 창고를 관리하는 주인공입니다. 배경은 현대아니라 판타지세계라 더 독특해요. 성좌채팅이 있어서 호불호 갈릴 수 있지만 이것도 스토리성이 있는 부분이라 후...",
                createdDate = "2023-08-14",
                isModified = false,
                isLiked = true,
                likeCount = 120,
                commentCount = 45,
                novelId = 1001,
                title = "그냥 뭐 재밌는 웹소설",
                novelRatingCount = 500,
                novelRating = 4.5f,
                relevantCategories = listOf("fantasy", "adventure")
            ),
            MyActivitiesEntity.MyActivityEntity(
                feedId = 2,
                userId = 101,
                profileImg = "https://github.com/user-attachments/assets/d4fa43d8-d90a-4ad9-bfa9-cc12fda05e1d",
                nickname = "샐리",
                isSpoiler = true,
                feedContent = "여주가 세계를 구함 이름이 나여주입니다ㅋㅋㅋ읽던 소설이 세계멸망엔딩나서 댓글달았다가 그 세계의 본인에게 빙의하게 되었는데 S급 힐러에 세계관 관련 중요스킬까지 얻고 시작하는 스토리. 121화 최신화 기준 큰 고구마없고 남주가 질서선 댕댕이...",
                createdDate = "2023-08-13",
                isModified = true,
                isLiked = false,
                likeCount = 30,
                commentCount = 12,
                novelId = 1002,
                title = "그냥 뭐 재밌는 웹소설",
                novelRatingCount = 250,
                novelRating = 3.9f,
                relevantCategories = listOf("drama", "romance")
            ),
            MyActivitiesEntity.MyActivityEntity(
                feedId = 3,
                userId = 101,
                profileImg = "https://github.com/user-attachments/assets/d4fa43d8-d90a-4ad9-bfa9-cc12fda05e1d",
                nickname = "샐리",
                isSpoiler = false,
                feedContent = "여주가 세계를 구함 이름이 나여주입니다ㅋㅋㅋ읽던 소설이 세계멸망엔딩나서 댓글달았다가 그 세계의 본인에게 빙의하게 되었는데 S급 힐러에 세계관 관련 중요스킬까지 얻고 시작하는 스토리. 121화 최신화 기준 큰 고구마없고 남주가 질서선 댕댕이입니다. 마스...",
                createdDate = "2023-08-12",
                isModified = false,
                isLiked = true,
                likeCount = 75,
                commentCount = 20,
                novelId = 1003,
                title = "그냥 뭐 재밌는 웹소설",
                novelRatingCount = 300,
                novelRating = 4.0f,
                relevantCategories = listOf("mystery", "thriller")
            ),
            MyActivitiesEntity.MyActivityEntity(
                feedId = 4,
                userId = 101,
                profileImg = "https://github.com/user-attachments/assets/d4fa43d8-d90a-4ad9-bfa9-cc12fda05e1d",
                nickname = "샐리",
                isSpoiler = false,
                feedContent = "판소추천해요! 완결난지는 좀 되었는데 추천합니다. scp 같은 이상현상 물품을 모아놓은 창고를 관리하는 주인공입니다. 배경은 현대아니라 판타지세계라 더 독특해요. 성좌채팅이 있어서 호불호 갈릴 수 있지만 이것도 스토리성이 있는 부분이라 후...",
                createdDate = "2023-08-11",
                isModified = false,
                isLiked = false,
                likeCount = 15,
                commentCount = 5,
                novelId = 1004,
                title = "그냥 뭐 재밌는 웹소설",
                novelRatingCount = 100,
                novelRating = 2.5f,
                relevantCategories = listOf("drama")
            )
        )
    }
}
