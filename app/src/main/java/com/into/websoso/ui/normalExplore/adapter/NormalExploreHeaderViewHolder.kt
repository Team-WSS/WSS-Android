package com.into.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemNormalExploreHeaderBinding

class NormalExploreHeaderViewHolder(
    private val binding: ItemNormalExploreHeaderBinding,
    onInquireClick: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.clNormalExploreInquireButton.setOnClickListener {
            onInquireClick.invoke()
        }
    }

    fun bind(novelCount: Long) {
        binding.apply {
            clNormalExploreNovelCount.isVisible = novelCount > NOVEL_COUNT_INVISIBLE_THRESHOLD
            tvNormalExploreNovelCount.text = novelCount.toString()
        }
    }

    companion object {
        private const val NOVEL_COUNT_INVISIBLE_THRESHOLD = 0

        fun from(
            parent: ViewGroup,
            onInquireClickListener: () -> Unit,
        ): NormalExploreHeaderViewHolder =
            NormalExploreHeaderViewHolder(
                ItemNormalExploreHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                onInquireClickListener,
            )
    }
}
