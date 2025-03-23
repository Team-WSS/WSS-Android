package com.into.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.load
import com.into.websoso.databinding.ItemFeedLoadingBinding
import com.into.websoso.resource.R.drawable.ic_load_load

class NormalExploreLoadingViewHolder(
    binding: ItemFeedLoadingBinding,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        val gifImageLoader = ImageLoader.Builder(binding.root.context)
            .components {
                add(ImageDecoderDecoder.Factory())
            }.build()
        binding.ivFeedLoading.load(ic_load_load, gifImageLoader)
    }

    companion object {

        fun from(parent: ViewGroup): NormalExploreLoadingViewHolder =
            NormalExploreLoadingViewHolder(
                ItemFeedLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
            )
    }
}
