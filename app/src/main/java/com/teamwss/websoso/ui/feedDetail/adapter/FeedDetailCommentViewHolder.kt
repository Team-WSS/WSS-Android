package com.teamwss.websoso.ui.feedDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemFeedDetailCommentBinding
import com.teamwss.websoso.ui.feedDetail.model.CommentModel

class FeedDetailCommentViewHolder(
    private val binding: ItemFeedDetailCommentBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: CommentModel) {

    }

    companion object {

        fun from(parent: ViewGroup): FeedDetailCommentViewHolder =
            FeedDetailCommentViewHolder(
                ItemFeedDetailCommentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
    }
}