package com.into.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemNormalExploreBinding
import com.into.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

class NormalExploreViewHolder(
    private val binding: ItemNormalExploreBinding,
    novelItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = novelItemClickListener
    }

    fun bind(normalExploreResult: NovelModel) {
        binding.novel = normalExploreResult
    }

    companion object {

        fun of(
            parent: ViewGroup,
            novelItemClickListener: (novelId: Long) -> Unit,
        ): NormalExploreViewHolder {
            val binding = ItemNormalExploreBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
            return NormalExploreViewHolder(binding, novelItemClickListener)
        }
    }
}