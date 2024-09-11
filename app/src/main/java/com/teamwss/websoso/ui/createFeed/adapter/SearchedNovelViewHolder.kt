package com.teamwss.websoso.ui.createFeed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemSearchNovelBinding
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

class SearchedNovelViewHolder(
    private val binding: ItemSearchNovelBinding,
    onNovelClick: (novelId: Long) -> (Unit),
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onNovelClick
    }

    fun bind(novel: NovelModel) {
        binding.novel = novel
        binding.ivCreateFeedSearchNovelSelected.isActivated = novel.isSelected
    }

    companion object {

        fun of(
            parent: ViewGroup, novelItemClickListener: (novelId: Long) -> Unit,
        ): SearchedNovelViewHolder {
            val binding = ItemSearchNovelBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
            return SearchedNovelViewHolder(binding, novelItemClickListener)
        }
    }
}
