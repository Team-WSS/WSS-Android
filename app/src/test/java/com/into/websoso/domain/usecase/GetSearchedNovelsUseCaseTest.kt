package com.into.websoso.domain.usecase

import com.into.websoso.data.model.ExploreResultEntity
import com.into.websoso.data.repository.NovelRepository
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSearchedNovelsUseCaseTest {
    private lateinit var getSearchedNovelsUseCase: GetSearchedNovelsUseCase
    private val novelRepository: NovelRepository = mockk(relaxed = true)

    private val dummyExploreResultEntity = ExploreResultEntity(
        resultCount = 0L,
        isLoadable = true,
        novels = emptyList(),
    )

    @Before
    fun setUp() {
        getSearchedNovelsUseCase = GetSearchedNovelsUseCase(novelRepository)
        coEvery {
            novelRepository.fetchNormalExploreResult(
                any(),
                any(),
                any(),
            )
        } returns dummyExploreResultEntity
    }

    @Test
    fun `처음 검색하면 페이지는 0, 사이즈는 20으로 레포지토리에 요청한다`() =
        runTest {
            // when
            getSearchedNovelsUseCase("웹소설")

            // then

            coVerify(exactly = 1) {
                novelRepository.fetchNormalExploreResult(
                    searchWord = "웹소설",
                    page = 0,
                    size = 20,
                )
            }
        }

    @Test
    fun `같은 검색어로 다시 검색하면 페이지는 1, 사이즈는 10으로 레포지토리에 요청한다`() =
        runTest {
            // when
            getSearchedNovelsUseCase("웹소설") // 0페이지 20개
            getSearchedNovelsUseCase("웹소설") // 1페이지 10개

            // then
            coVerify(exactly = 1) {
                novelRepository.fetchNormalExploreResult(
                    searchWord = "웹소설",
                    page = 1,
                    size = 10,
                )
            }
        }

    @Test
    fun `다른 검색어로 검색하면 캐시를 지우고 페이지는 0으로 레포지토리에 요청한다`() =
        runTest {
            // when
            getSearchedNovelsUseCase("웹소설") // clear 1회
            clearMocks(novelRepository, answers = false)
            getSearchedNovelsUseCase("새로운 웹소설")

            // then
            coVerify(exactly = 1) {
                novelRepository.clearCachedNormalExploreResult()
                novelRepository.fetchNormalExploreResult(
                    searchWord = "새로운 웹소설",
                    page = 0,
                    size = 20,
                )
            }
        }
}
