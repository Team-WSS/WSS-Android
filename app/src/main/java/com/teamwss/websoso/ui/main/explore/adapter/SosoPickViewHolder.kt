package com.teamwss.websoso.ui.main.explore.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.SosoPickEntity
import com.teamwss.websoso.databinding.ItemSosoPickBinding

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