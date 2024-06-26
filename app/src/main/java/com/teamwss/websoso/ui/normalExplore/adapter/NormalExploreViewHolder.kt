package com.teamwss.websoso.ui.normalExplore.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.NormalExploreEntity
import com.teamwss.websoso.databinding.ItemNormalExploreBinding

class NormalExploreViewHolder(
    private val binding: ItemNormalExploreBinding,
    novelItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = novelItemClickListener
    }

    fun onBind(
        normalExploreResult: NormalExploreEntity.NovelEntity,
    ) {
        binding.novel = normalExploreResult
    }
}