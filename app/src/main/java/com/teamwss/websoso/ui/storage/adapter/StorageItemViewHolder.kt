package com.teamwss.websoso.ui.storage.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.StorageEntity
import com.teamwss.websoso.databinding.ItemStorageNovelBinding

class StorageItemViewHolder(
    private val binding: ItemStorageNovelBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(novel: StorageEntity.UserNovel) {
        val isRatingVisible = novel.novelRating != 0.0
        binding.novel = novel
        binding.isRatingVisible = isRatingVisible
    }
}