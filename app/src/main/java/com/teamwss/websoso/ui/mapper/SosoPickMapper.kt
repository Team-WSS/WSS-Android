package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.SosoPickEntity
import com.teamwss.websoso.ui.main.explore.model.SosoPickModel

object SosoPickMapper {

    fun SosoPickEntity.toPresentation(): SosoPickModel = SosoPickModel(
        novelId = novelId,
        novelTitle = novelTitle,
        novelCover = novelCover,
    )
}