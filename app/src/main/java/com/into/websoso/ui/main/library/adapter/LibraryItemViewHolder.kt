package com.into.websoso.ui.main.library.adapter

import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemStorageNovelBinding
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class LibraryItemViewHolder(
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
