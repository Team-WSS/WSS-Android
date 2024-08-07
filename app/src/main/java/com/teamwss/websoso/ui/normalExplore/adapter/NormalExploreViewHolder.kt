package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.NormalExploreEntity.NovelEntity
import com.teamwss.websoso.databinding.ItemNormalExploreBinding

class NormalExploreViewHolder(
    private val binding: ItemNormalExploreBinding,
    novelItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = novelItemClickListener
    }

    fun bind(normalExploreResult: NovelEntity) {
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