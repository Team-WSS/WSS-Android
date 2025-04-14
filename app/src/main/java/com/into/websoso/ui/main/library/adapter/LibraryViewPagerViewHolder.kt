package com.into.websoso.ui.main.library.adapter

import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemStorageBinding
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class LibraryViewPagerViewHolder(
    private val binding: ItemStorageBinding,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private val adapter: LibraryItemAdapter by lazy {
        LibraryItemAdapter(emptyList(), novelClickListener)
    }

    init {
        binding.rvStorage.adapter = adapter
    }

    fun bind(novels: List<StorageNovelModel>) {
        if (novels.isNotEmpty()) {
            adapter.updateNovels(novels)
        }
    }
}
