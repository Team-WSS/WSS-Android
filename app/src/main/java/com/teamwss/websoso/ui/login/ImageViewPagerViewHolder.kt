package com.teamwss.websoso.ui.login

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemLoginImageBinding

class ImageViewPagerViewHolder(private val binding: ItemLoginImageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(imageRes: Int) {
        binding.imageRes = imageRes
    }
}