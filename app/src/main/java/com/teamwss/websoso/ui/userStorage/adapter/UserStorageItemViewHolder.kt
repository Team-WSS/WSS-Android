package com.teamwss.websoso.ui.userStorage.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemStorageNovelBinding
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageItemViewHolder(
    private val binding: ItemStorageNovelBinding,
    private var novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(novel: StorageNovelModel) {
        binding.novel = novel
        itemView.setOnClickListener {
            novelClickListener(novel.novelId)
        }
    }
}