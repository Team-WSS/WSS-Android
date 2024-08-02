package com.teamwss.websoso.ui.normalExplore.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.databinding.ItemNormalExploreBinding

class NormalExploreViewHolder(
    private val binding: ItemNormalExploreBinding,
    novelItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = novelItemClickListener
    }

    fun onBind(normalExploreResult: ExploreResultEntity.NovelEntity) {
        binding.novel = normalExploreResult
    }
}