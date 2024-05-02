package com.teamwss.websoso.ui.main.explore.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemSosoPickBinding
import com.teamwss.websoso.ui.main.explore.model.SosoPickModel

class SosoPickViewHolder(
    private val binding: ItemSosoPickBinding,
    private val sosoPickItemClickListener: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(sosoPick: SosoPickModel) {
        binding.sosoPick = sosoPick
        binding.root.setOnClickListener {
            sosoPickItemClickListener(sosoPick.novelId)
        }
    }
}