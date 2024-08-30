package com.teamwss.websoso.ui.createFeed.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemSearchNovelBinding
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

class SearchedNovelViewHolder(
    private val binding: ItemSearchNovelBinding,
    onNovelClick: (novelId: Long) -> (Unit),
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(novel: NovelModel) {
        Log.d("123123", novel.toString())
        binding.novel = novel
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
