package com.teamwss.websoso.ui.main.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemFeedNoMoreBinding

class FeedNoMoreViewHolder(
    binding: ItemFeedNoMoreBinding,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {

        fun from(parent: ViewGroup): FeedNoMoreViewHolder =
            FeedNoMoreViewHolder(
                ItemFeedNoMoreBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
    }
}
