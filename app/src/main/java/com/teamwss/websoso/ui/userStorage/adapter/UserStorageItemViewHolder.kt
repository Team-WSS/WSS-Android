package com.teamwss.websoso.ui.userStorage.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemStorageNovelBinding
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageItemViewHolder(
    private val binding: ItemStorageNovelBinding,
    private var novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var novelId: Long? = null

    init {
        itemView.setOnClickListener {
            novelId?.let { id ->
                novelClickListener(id)
            }
        }
    }

    fun bind(novel: StorageNovelModel) {
        binding.novel = novel
        novelId = novel.novelId
    }
}