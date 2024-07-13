package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto
import javax.inject.Inject

class FakeNovelInfoRepository @Inject constructor() {

    suspend fun fetchNovelInfo(novelId: Long): NovelInfoEntity {
        return dummyNovelInfo.toData()
    }

    private val dummyNovelInfo = NovelInfoResponseDto(
        novelDescription = "왕실에는 막대한 빚이 있었고, 그들은 빚을 갚기 위해\n왕녀인 바이올렛을 막대한 돈을 지녔지만 공작의 사생아인\n윈터에게 시집보낸다.\n\n\'태어나서 이렇게 멋있는 남자는 처음 봐…….\'\n\n다행히 바이올렛은 정략 결혼 상대에게 첫 눈에 반하지만\n두 사람의 결혼 생활은 처음부터 어긋나고.\n\n\"쉬운 일이었으면 당신에게 말하러 오지도 않았어요. 이번\n한 번만 같이…….\"\n\"당신이 여기서 고집부리며 내 시간을 허비하는 사이에 얼\n마나 많은 돈이 움직였는지 알아?\"\n\n그로부터 3년. 바이올렛은 저 바쁜 남자가\n제 장례식이라고 와 줄지에 대해조차 확신할 수 없다.\n그렇게 그녀가 이혼을 결심했을 때,\n\n\"뭐가 어떻게 된 거야…….\"\n\n바이올렛이 멍한 얼굴로 침실에 있는 전신 거울에 제 모습\n을 비춰 보았다.\n거울 속 사내는 분명 남편인 윈터 블루밍이었다.\n그런데 어째서 자신과 남편의 몸이 뒤바뀌게 된 것일까?\n\n\"이제 진짜로 미쳐 버렸나 봐.",
        platforms =
        listOf(
            NovelInfoResponseDto.PlatformResponseDto(
                platformName = "네이버시리즈",
                platformImage = "https://ssl.pstatic.net/static/nstore/ogtag_series_v2.png",
                platformUrl = "https://www.naver.com",
            ),
        ),
        attractivePoints = listOf("소재", "관계", "분위기"),
        keywords =
        listOf(
            NovelInfoResponseDto.KeywordResponseDto(
                keywordName = "서양풍/중세시대",
                keywordCount = 5,
            ),
            NovelInfoResponseDto.KeywordResponseDto(
                keywordName = "웹툰화",
                keywordCount = 4,
            ),
            NovelInfoResponseDto.KeywordResponseDto(
                keywordName = "동양풍/사극",
                keywordCount = 4,
            ),
        ),
        watchingCount = 36,
        watchedCount = 5,
        quitCount = 2,
    )
}