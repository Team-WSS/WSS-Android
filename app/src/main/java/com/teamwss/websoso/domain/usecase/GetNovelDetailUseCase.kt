package com.teamwss.websoso.domain.usecase

import android.content.Context
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.NovelDetail
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetNovelDetailUseCase @Inject constructor(
    private val novelRepository: NovelRepository,
    @ApplicationContext private val context: Context,
) {

    private val serverGenres: List<String> by lazy {
        context.resources.getString(R.string.novel_detail_genre_api).split(",")
    }

    private val koreanUiGenres: List<String> by lazy {
        context.resources.getString(R.string.novel_detail_genre_ui_kr).split(",")
    }

    private val genreMap: Map<String, String> by lazy {
        serverGenres.zip(koreanUiGenres).toMap()
    }

    private fun formattedGenres(genres: List<String>): List<String> {
        return genres.mapNotNull { genreMap[it] }
    }

    suspend fun execute(novelId: Long): NovelDetail {
        val novelDetailEntity = novelRepository.getNovelDetail(novelId)
        return novelDetailEntity.toDomain(
            formattedGenres(novelDetailEntity.novelGenres.split(",")),
            when (novelDetailEntity.isNovelCompleted) {
                true -> context.getString(R.string.novel_detail_completed)
                false -> context.getString(R.string.novel_detail_in_series)
            },
        )
    }
}
