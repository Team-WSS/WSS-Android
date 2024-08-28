package com.teamwss.websoso.ui.feedDetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.teamwss.websoso.ui.feedDetail.CommentClickListener
import com.teamwss.websoso.ui.feedDetail.FeedDetailClickListener
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Comment
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Header
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.ItemType
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.ItemType.COMMENT
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.ItemType.HEADER

class FeedDetailAdapter(
    private val feedDetailClickListener: FeedDetailClickListener,
    private val commentClickListener: CommentClickListener,
) : ListAdapter<FeedDetailType, ViewHolder>(diffCallBack) {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Header -> HEADER.ordinal
        is Comment -> COMMENT.ordinal
    }

    override fun getItemId(position: Int): Long = when (getItem(position)) {
        is Header -> super.getItemId(position)
        is Comment -> (getItem(position) as Comment).comment.commentId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (ItemType.valueOf(viewType)) {
            HEADER -> FeedDetailContentViewHolder.from(parent, feedDetailClickListener)
            COMMENT -> FeedDetailCommentViewHolder.from(parent, commentClickListener)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is FeedDetailContentViewHolder -> holder.bind(
                (getItem(position) as Header).feed,
                itemCount - 1,
            )

            is FeedDetailCommentViewHolder -> holder.bind((getItem(position) as Comment).comment)
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<FeedDetailType>() {
            override fun areItemsTheSame(
                oldItem: FeedDetailType,
                newItem: FeedDetailType,
            ): Boolean = when {
                (oldItem is Comment) and (newItem is Comment) ->
                    (oldItem as Comment).comment.commentId == (newItem as Comment).comment.commentId

                else -> oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: FeedDetailType,
                newItem: FeedDetailType,
            ): Boolean = oldItem == newItem
        }
    }
}
