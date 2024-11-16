package com.teamwss.websoso.ui.userStorage.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.common.util.InfiniteScrollListener
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.ItemStorageBinding
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageViewPagerViewHolder(
    private val binding: ItemStorageBinding,
    private val novelClickListener: (novelId: Long) -> Unit,
    private val loadMoreCallback: () -> Unit,
    val singleEventHandler: SingleEventHandler
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter: UserStorageItemAdapter by lazy {
        UserStorageItemAdapter(mutableListOf(), novelClickListener)
    }

    private val layoutManager: GridLayoutManager by lazy {
        GridLayoutManager(binding.root.context, STORAGE_NOVEL_SPAN_COUNT)
    }

    init {
        binding.rvStorage.adapter = adapter
        binding.rvStorage.layoutManager = layoutManager

        binding.rvStorage.apply {
            itemAnimator = null
            addOnScrollListener(
                InfiniteScrollListener.of(
                    singleEventHandler = singleEventHandler,
                    event = loadMoreCallback,
                )
            )
            setHasFixedSize(true)
        }
    }

    fun bind(novels: List<StorageNovelModel>) {
        adapter.updateNovels(novels)
    }

    companion object {
        const val STORAGE_NOVEL_SPAN_COUNT = 3
    }
}