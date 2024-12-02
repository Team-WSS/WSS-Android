package com.into.websoso.ui.login.adapter

import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemLoginImageBinding

class ImageViewPagerViewHolder(private val binding: ItemLoginImageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(imageRes: Int) {
        binding.imageRes = imageRes
    }
}