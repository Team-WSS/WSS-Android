package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.FakeUserRepository

class GetCategoryByUserGenderUseCase(
    private val fakeUserRepository: FakeUserRepository,
) {
    operator fun invoke(): String =
        when (fakeUserRepository.gender) {
            "MALE" -> MALE_CATEGORY
            "FEMALE" -> FEMALE_CATEGORY
            else -> throw IllegalArgumentException()
        }

    companion object {
        private const val MALE_CATEGORY = "전체,판타지,현판,무협,드라마,미스터리,라노벨,로맨스,로판,BL,기타"
        private const val FEMALE_CATEGORY = "전체,로맨스,로판,BL,판타지,현판,무협,드라마,미스터리,라노벨,기타"
    }
}
