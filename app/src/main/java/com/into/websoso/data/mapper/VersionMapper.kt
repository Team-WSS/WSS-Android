package com.into.websoso.data.mapper

import com.into.websoso.data.model.MinimumVersionEntity
import com.into.websoso.data.remote.response.MinimumVersionResponseDto

fun MinimumVersionResponseDto.toData(): MinimumVersionEntity =
    MinimumVersionEntity(
        minimumVersion = minimumVersion,
    )