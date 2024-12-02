package com.into.websoso.domain.model

data class Feeds(
    val category: String,
    val isLoadable: Boolean,
    val feeds: List<Feed>,
)
