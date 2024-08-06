package com.teamwss.websoso.ui.detailExploreResult.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.ExploreResultEntity.NovelEntity
import com.teamwss.websoso.databinding.ItemDetailExploreResultBinding

class DetailExploreResultViewHolder(
    private val binding: ItemDetailExploreResultBinding,
    onNovelClick: (novelId: Long) -> (Unit),
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onNovelClick
    }

    fun bind(result: NovelEntity) {
        binding.novel = result
    }

    companion object {

        fun of(
            parent: ViewGroup, novelItemClickListener: (novelId: Long) -> Unit,
        ): DetailExploreResultViewHolder {
            val binding = ItemDetailExploreResultBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
            return DetailExploreResultViewHolder(binding, novelItemClickListener)
        }
    }
}