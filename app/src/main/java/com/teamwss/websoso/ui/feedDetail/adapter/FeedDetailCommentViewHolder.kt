package com.teamwss.websoso.ui.feedDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.databinding.ItemFeedDetailCommentBinding
import com.teamwss.websoso.ui.feedDetail.CommentClickListener
import com.teamwss.websoso.ui.feedDetail.model.CommentModel

class FeedDetailCommentViewHolder(
    commentClickListener: CommentClickListener,
    private val binding: ItemFeedDetailCommentBinding,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = commentClickListener
    }

    fun bind(comment: CommentModel) {
        binding.comment = comment.copy(
            user = comment.user.copy(avatarImage = itemView.getS3ImageUrl(comment.user.avatarImage))
        )
    }

    companion object {

        fun from(
            parent: ViewGroup,
            commentClickListener: CommentClickListener,
        ): FeedDetailCommentViewHolder = FeedDetailCommentViewHolder(
            commentClickListener,
            ItemFeedDetailCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }
}
