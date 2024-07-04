package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import javax.inject.Inject

class FakeFeedRepository
    @Inject
    constructor() {
        val dummyData: FeedsEntity =
            FeedsEntity(
                "Default",
                listOf(
                    FeedEntity(
                        user =
                            FeedEntity.UserEntity(
                                id = 1,
                                nickname = "로판처돌이",
                                profileImage = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1",
                            ),
                        createdDate = "3월 21일",
                        id = 1,
                        content =
                            "판소추천해요!\n" +
                                "완결난지는 좀 되었는데 추천합니다. scp 같은 이상현상 물품을 모아놓은 창고를 관리하는 주인공입니다. 배경은 현대아니라 판타지세계라 더 독특해요. 성좌채팅이 있어서 호불호 갈릴 수 있지만 이것도 스토리성이 있는 부분이라 후 잘 모르겠쒀요",
                        relevantCategories = listOf("무협", "로맨스"),
                        likeCount = "10",
                        likeUsers = listOf(1, 102, 103),
                        commentCount = "3",
                        isModified = false,
                        isSpoiled = false,
                        novel =
                            FeedEntity.NovelEntity(
                                id = 1,
                                title = "화산귀환",
                                rating = 4.5,
                                ratingCount = 200,
                            ),
                    ),
                    FeedEntity(
                        user =
                            FeedEntity.UserEntity(
                                id = 2,
                                nickname = "죽은군",
                                profileImage = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1",
                            ),
                        createdDate = "3월 28일",
                        id = 2,
                        content =
                            "여주가 세계를 구함\n" +
                                "이름이 나여주입니다ㅋㅋㅋ읽던 소설이 세계멸망엔딩나서 댓글달았다가 그 세계의 본인에게 빙의하게 되었는데 S급 힐러에 세계관 관련 중요스킬까지 얻고 시작하는 스토리. 121화 최신화 기준 큰 고구마없고 남주가 질서선 댕댕이 기여워요",
                        relevantCategories = listOf("로판", "BL"),
                        likeCount = "5",
                        likeUsers = listOf(104, 105),
                        commentCount = "2",
                        isModified = false,
                        isSpoiled = true,
                        novel =
                            FeedEntity.NovelEntity(
                                id = 2,
                                title = "눈물의 여왕",
                                rating = 3.8,
                                ratingCount = 150,
                            ),
                    ),
                ),
            )
    }
