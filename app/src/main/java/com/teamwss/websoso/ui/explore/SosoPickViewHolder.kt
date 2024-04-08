package com.teamwss.websoso.ui.explore

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.teamwss.websoso.databinding.ItemSosoPickBinding

class SosoPickViewHolder(
    private val binding: ItemSosoPickBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(sosoPickData: SosoPickData) {
        binding.tvExploreSosoPickNovelTitle.text = sosoPickData.title
        binding.ivExploreSosoPickNovelThumbnail.load(sosoPickData.image)
    }
}