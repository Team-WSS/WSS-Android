package com.teamwss.websoso.ui.feedDetail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.R.color.black
import com.teamwss.websoso.R.color.gray_200_AEADB3
import com.teamwss.websoso.R.color.secondary_100_FF675D
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
            user = comment.user.copy(
                avatarImage = itemView.getS3ImageUrl(comment.user.avatarImage),
            ),
        )
        binding.tvFeedUserName.text = comment.user.nickname
        binding.tvFeedDetailCommentContent.text = comment.commentContent
        updateUiByCase(comment)
    }

    @SuppressLint("ResourceAsColor")
    private fun updateUiByCase(comment: CommentModel) {
        if (comment.isBlocked) {
            binding.tvFeedUserName.text = "차단한 유저"
            binding.tvFeedDetailCommentContent.text = "차단한 유저의 댓글"
            binding.tvFeedDetailCommentContent.setTextColor(gray_200_AEADB3.color())
            binding.ivFeedDetailMoreButton.isVisible = false
            return
        }

        if (comment.isHidden) {
            binding.tvFeedDetailCommentContent.text = "숨김 처리된 댓글"
            binding.tvFeedDetailCommentContent.setTextColor(gray_200_AEADB3.color())
            binding.ivFeedDetailMoreButton.isVisible = false
            return
        }

        if (comment.isSpoiler) {
            binding.tvFeedDetailCommentContent.apply {
                text = "스포일러가 포함된 댓글 보기"
                setTextColor(secondary_100_FF675D.color())
                setOnClickListener {
                    setTextColor(black.color())
                    text = comment.commentContent
                }
            }
        }
    }

    private fun Int.color(): Int = binding.root.context.getColor(this)

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
