package com.teamwss.websoso.ui.main.explore.model

data class SosoPickData(
    val title: String,
    val image: String,
) {
    companion object {
        val sosoMockData: MutableList<SosoPickData> = mutableListOf(
            SosoPickData(
                "상수리 나무 아래",
                "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ),
            SosoPickData(
                "악역의 엔딩은 죽음뿐",
                "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ),
            SosoPickData(
                "나혼자만 레벨업",
                "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ),
            SosoPickData(
                "전지적 독자 시점",
                "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ),
            SosoPickData(
                "신이 쓰는 웹소설",
                "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ),
            SosoPickData(
                "악녀는 마리오네트",
                "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            ),
            SosoPickData(
                "역대급 영지 설계사입니다 주인공 얼굴 진짜 대박",
                "https://page-images.kakaoentcdn.com/download/resource?kid=LWSLy/hzN2my4ybO/6AFasaBRdiBRR4RRswKqU1&filename=o1"
            )
        )
    }
}