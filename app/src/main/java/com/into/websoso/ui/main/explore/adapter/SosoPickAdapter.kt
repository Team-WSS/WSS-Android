package com.into.websoso.ui.main.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.into.websoso.data.model.SosoPickEntity
import com.into.websoso.databinding.ItemSosoPickBinding

class SosoPickAdapter(
    private val sosoPickItemClickListener: (novelId: Long) -> Unit,
) : ListAdapter<SosoPickEntity.NovelEntity, SosoPickViewHolder>(SosoPickDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SosoPickViewHolder {
        val binding =
            ItemSosoPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SosoPickViewHolder(binding, sosoPickItemClickListener)
    }

    override fun onBindViewHolder(
        holder: SosoPickViewHolder,
        position: Int,
    ) {
        holder.onBind(getItem(position))
    }
}

class SosoPickDiffCallback : DiffUtil.ItemCallback<SosoPickEntity.NovelEntity>() {
    override fun areItemsTheSame(
        oldItem: SosoPickEntity.NovelEntity,
        newItem: SosoPickEntity.NovelEntity,
    ): Boolean = oldItem.novelId == newItem.novelId

    override fun areContentsTheSame(
        oldItem: SosoPickEntity.NovelEntity,
        newItem: SosoPickEntity.NovelEntity,
    ): Boolean = oldItem == newItem
}
