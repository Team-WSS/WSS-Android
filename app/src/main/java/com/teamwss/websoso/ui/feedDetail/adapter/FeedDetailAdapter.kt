package com.teamwss.websoso.ui.feedDetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Comment
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Header
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.ItemType

class FeedDetailAdapter : ListAdapter<FeedDetailType, ViewHolder>(diffCallBack) {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Header -> ItemType.HEADER.ordinal
        is Comment -> ItemType.COMMENT.ordinal
    }

    override fun getItemId(position: Int): Long = when (getItem(position)) {
        is Header -> super.getItemId(position)
        is Comment -> (getItem(position) as Comment).comment.commentId.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (ItemType.valueOf(viewType)) {
            ItemType.HEADER -> FeedDetailContentViewHolder.from(parent)
            ItemType.COMMENT -> FeedDetailCommentViewHolder.from(parent)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is FeedDetailContentViewHolder -> holder.bind((getItem(position) as Header).feed)
            is FeedDetailCommentViewHolder -> holder.bind((getItem(position) as Comment).comment)
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<FeedDetailType>() {
            override fun areItemsTheSame(
                oldItem: FeedDetailType,
                newItem: FeedDetailType,
            ): Boolean =
                when {
                    (oldItem is Header) and (newItem is Comment) -> false
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
