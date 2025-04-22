package com.into.websoso.ui.detailExploreResult.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemDetailExploreResultHeaderBinding

class DetailExploreResultHeaderViewHolder(
    private val binding: ItemDetailExploreResultHeaderBinding,
    onInquireClick: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.clDetailExploreInquireButton.setOnClickListener {
            onInquireClick.invoke()
        }
    }

    fun bind(novelCount: Long) {
        binding.apply {
            clDetailExploreNovelCount.isVisible = novelCount > NOVEL_COUNT_INVISIBLE_THRESHOLD
            tvDetailExploreNovelCount.text = novelCount.toString()
        }
    }

    companion object {
        private const val NOVEL_COUNT_INVISIBLE_THRESHOLD = 0

        fun from(
            parent: ViewGroup,
            onInquireClickListener: () -> Unit,
        ): DetailExploreResultHeaderViewHolder =
            DetailExploreResultHeaderViewHolder(
                ItemDetailExploreResultHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                onInquireClickListener,
            )
    }
}
