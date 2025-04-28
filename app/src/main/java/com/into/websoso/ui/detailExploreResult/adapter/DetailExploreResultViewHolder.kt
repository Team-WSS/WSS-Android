package com.into.websoso.ui.detailExploreResult.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemDetailExploreResultBinding
import com.into.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

class DetailExploreResultViewHolder(
    private val binding: ItemDetailExploreResultBinding,
    onNovelClick: (novelId: Long) -> (Unit),
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = onNovelClick
    }

    fun bind(result: NovelModel) {
        binding.novel = result
    }

    companion object {
        fun of(
            parent: ViewGroup,
            novelItemClickListener: (novelId: Long) -> Unit,
        ): DetailExploreResultViewHolder {
            val binding = ItemDetailExploreResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return DetailExploreResultViewHolder(binding, novelItemClickListener)
        }
    }
}
