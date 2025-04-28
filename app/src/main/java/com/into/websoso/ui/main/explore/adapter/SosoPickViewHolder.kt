package com.into.websoso.ui.main.explore.adapter

import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.data.model.SosoPickEntity
import com.into.websoso.databinding.ItemSosoPickBinding

class SosoPickViewHolder(
    private val binding: ItemSosoPickBinding,
    sosoPickItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.sosoPickItemClickListener = sosoPickItemClickListener
    }

    fun onBind(sosoPick: SosoPickEntity.NovelEntity) {
        binding.sosoPick = sosoPick
    }
}
