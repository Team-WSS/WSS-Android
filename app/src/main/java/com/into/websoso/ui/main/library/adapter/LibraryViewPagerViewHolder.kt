package com.into.websoso.ui.main.library.adapter

import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.databinding.ItemStorageBinding
import com.into.websoso.ui.userStorage.model.UserStorageModel
import kotlinx.coroutines.flow.Flow

class LibraryViewPagerViewHolder(
    binding: ItemStorageBinding,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private val adapter2: LibraryItemAdapter2 by lazy {
        LibraryItemAdapter2(novelClickListener)
    }

    init {
        binding.rvStorage.adapter = adapter2
    }

    fun bind(
        novels: Flow<PagingData<NovelEntity>>,
        scope: LifecycleOwner,
    ) {
        novels.collectWithLifecycle(scope) {
            adapter2.submitData(it.map(NovelEntity::toModel))
        }
    }
}

fun NovelEntity.toModel(): UserStorageModel.StorageNovelModel =
    UserStorageModel.StorageNovelModel(
        author = author,
        novelId = novelId,
        novelImage = novelImage,
        novelRating = novelRating,
        title = title,
        userNovelId = userNovelId,
    )
