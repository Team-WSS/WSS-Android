package com.into.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity.RecommendedNovelByUserTasteEntity
import com.into.websoso.databinding.ItemRecommendedNovelByUserTasteBinding

class RecommendedNovelsByUserTasteViewHolder(
    private val binding: ItemRecommendedNovelByUserTasteBinding,
    onRecommendedNovelClick: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onRecommendedNovelClick
    }

    fun bind(recommendedNovelByUserTaste: RecommendedNovelByUserTasteEntity) {
        binding.recommendedNovel = recommendedNovelByUserTaste
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onRecommendedNovelClick: (novelId: Long) -> Unit,
        ): RecommendedNovelsByUserTasteViewHolder {
            val binding = ItemRecommendedNovelByUserTasteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
            return RecommendedNovelsByUserTasteViewHolder(binding, onRecommendedNovelClick)
        }
    }
}