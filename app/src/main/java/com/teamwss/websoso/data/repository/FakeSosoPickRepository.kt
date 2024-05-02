package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.SosoPickEntity
import com.teamwss.websoso.data.model.SosoPicksEntity

class FakeSosoPickRepository {

    val dummyData: SosoPicksEntity = SosoPicksEntity(
        listOf(
            SosoPickEntity(
                novelId = 1,
                novelTitle = "상수리 나무 아래",
                novelCover = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ), SosoPickEntity(
                novelId = 2,
                novelTitle = "악역의 엔딩은 죽음뿐",
                novelCover = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ), SosoPickEntity(
                novelId = 3,
                novelTitle = "나혼자만 레벨업",
                novelCover = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ), SosoPickEntity(
                novelId = 4,
                novelTitle = "전지적 독자 시점",
                novelCover = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ), SosoPickEntity(
                novelId = 5,
                novelTitle = "신이 쓰는 웹소설",
                novelCover = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ), SosoPickEntity(
                novelId = 6,
                novelTitle = "악녀는 마리오네트",
                novelCover = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ), SosoPickEntity(
                novelId = 7,
                novelTitle = "역대급 영지 설계사입니다 주인공 얼굴 진짜 대박",
                novelCover = "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            )
        )
    )
}