package com.teamwss.websoso.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemLoginImageBinding

class ImageViewPagerAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<ImageViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoginImageBinding.inflate(inflater, parent, false)
        return ImageViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewPagerViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount() = images.size
}
