package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemNormalExploreResultCountBinding

class NormalExploreHeaderViewHolder(private val binding: ItemNormalExploreResultCountBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(novelCount: Int) {
        binding.apply {
            clNormalExploreNovelCount.isVisible = novelCount > NOVEL_COUNT_INVISIBLE_THRESHOLD
            tvNormalExploreNovelCount.text = novelCount.toString()
        }
    }

    companion object {
        private const val NOVEL_COUNT_INVISIBLE_THRESHOLD = 0

        fun from(parent: ViewGroup): NormalExploreHeaderViewHolder =
            NormalExploreHeaderViewHolder(
                ItemNormalExploreResultCountBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
    }
}