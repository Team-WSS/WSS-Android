package com.teamwss.websoso.ui.normalExplore.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemNormalExploreResultCountBinding

class NormalExploreCountViewHolder(private val binding: ItemNormalExploreResultCountBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(novelCount: Int) {
        binding.tvNormalExploreNovelCount.text = novelCount.toString()
    }
}