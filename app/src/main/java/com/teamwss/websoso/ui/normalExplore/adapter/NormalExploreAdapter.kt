package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.data.model.NormalExploreEntity
import com.teamwss.websoso.databinding.ItemNormalExploreBinding

class NormalExploreAdapter(
    private val novelItemClickListener: (novelId: Long) -> Unit
) : ListAdapter<NormalExploreEntity.NovelEntity, NormalExploreViewHolder>(NormalExploreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalExploreViewHolder {
        val binding =
            ItemNormalExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalExploreViewHolder(binding, novelItemClickListener)
    }

    override fun onBindViewHolder(holder: NormalExploreViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class NormalExploreDiffCallback : DiffUtil.ItemCallback<NormalExploreEntity.NovelEntity>() {

        override fun areItemsTheSame(
            oldItem: NormalExploreEntity.NovelEntity,
            newItem: NormalExploreEntity.NovelEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NormalExploreEntity.NovelEntity,
            newItem: NormalExploreEntity.NovelEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}