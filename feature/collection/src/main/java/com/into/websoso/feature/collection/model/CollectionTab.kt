package com.into.websoso.feature.collection.model

import androidx.annotation.StringRes
import com.into.websoso.core.resource.R.string.collection_liked
import com.into.websoso.core.resource.R.string.collection_my

internal enum class CollectionTab(
    @get:StringRes val titleRes: Int,
) {
    MY_COLLECTION(collection_my),
    LIKED_COLLECTION(collection_liked),
}
