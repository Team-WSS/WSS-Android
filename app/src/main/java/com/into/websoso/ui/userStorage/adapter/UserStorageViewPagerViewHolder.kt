package com.into.websoso.ui.userStorage.adapter

import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemStorageBinding
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageViewPagerViewHolder(
    private val binding: ItemStorageBinding,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private val adapter: UserStorageItemAdapter by lazy {
        UserStorageItemAdapter(emptyList(), novelClickListener)
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
