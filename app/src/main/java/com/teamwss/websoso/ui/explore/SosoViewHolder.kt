package com.teamwss.websoso.ui.explore

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.teamwss.websoso.databinding.ItemSosoPickBinding

class SosoViewHolder(
    private val binding: ItemSosoPickBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(sosoData: SosoData) {
        binding.tvExploreSosoPickNovelTitle.text = sosoData.title
        binding.ivExploreSosoPickNovelThumbnail.load(sosoData.image)
    }
}