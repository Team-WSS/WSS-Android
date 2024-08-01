package com.teamwss.websoso.ui.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.load
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ItemFeedLoadingBinding

class FeedLoadingViewHolder(
    binding: ItemFeedLoadingBinding,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        val gifImageLoader = ImageLoader.Builder(binding.root.context)
            .components {
                add(ImageDecoderDecoder.Factory())
            }
            .build()
        binding.ivFeedLoading.load(R.drawable.ic_load_load, gifImageLoader)
    }

    companion object {

        fun from(parent: ViewGroup): FeedLoadingViewHolder =
            FeedLoadingViewHolder(
                ItemFeedLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
    }
}
