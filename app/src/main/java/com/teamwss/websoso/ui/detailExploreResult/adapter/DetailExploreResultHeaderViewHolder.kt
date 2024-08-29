package com.teamwss.websoso.ui.detailExploreResult.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemDetailExploreResultHeaderBinding

class DetailExploreResultHeaderViewHolder(private val binding: ItemDetailExploreResultHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(novelCount: Long) {
        binding.apply {
            clDetailExploreNovelCount.isVisible = novelCount > NOVEL_COUNT_INVISIBLE_THRESHOLD
            tvDetailExploreNovelCount.text = novelCount.toString()
        }
    }

    companion object {
        private const val NOVEL_COUNT_INVISIBLE_THRESHOLD = 0

        fun from(parent: ViewGroup): DetailExploreResultHeaderViewHolder =
            DetailExploreResultHeaderViewHolder(
                ItemDetailExploreResultHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
    }
}