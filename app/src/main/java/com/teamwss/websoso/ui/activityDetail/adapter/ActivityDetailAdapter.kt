package com.teamwss.websoso.ui.activityDetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.ui.activityDetail.model.ActivityType
import com.teamwss.websoso.ui.main.feed.adapter.FeedLoadingViewHolder
import com.teamwss.websoso.ui.main.feed.adapter.FeedNoMoreViewHolder
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener

class ActivityDetailAdapter(
    private val activityItemClickListener: ActivityItemClickListener,
) : ListAdapter<ActivityType, RecyclerView.ViewHolder>(diffCallBack) {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ActivityType.Activity -> ActivityType.ItemType.ACTIVITY.ordinal
        is ActivityType.Loading -> ActivityType.ItemType.LOADING.ordinal
        is ActivityType.NoMore -> ActivityType.ItemType.NO_MORE.ordinal
    }

    override fun getItemId(position: Int): Long = when (getItem(position)) {
        is ActivityType.Activity -> (getItem(position) as ActivityType.Activity).activity.activity.feedId
        else -> super.getItemId(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (ActivityType.ItemType.valueOf(viewType)) {
            ActivityType.ItemType.ACTIVITY -> ActivityDetailViewHolder.of(
                parent,
                activityItemClickListener
            )

            ActivityType.ItemType.LOADING -> FeedLoadingViewHolder.from(parent)
            ActivityType.ItemType.NO_MORE -> FeedNoMoreViewHolder.from(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActivityDetailViewHolder -> {
                val activity = (getItem(position) as ActivityType.Activity).activity
                holder.bind(activity)
            }
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<ActivityType>() {
            override fun areItemsTheSame(oldItem: ActivityType, newItem: ActivityType): Boolean =
                when ((oldItem is ActivityType.Activity) and (newItem is ActivityType.Activity)) {
                    true -> (oldItem as ActivityType.Activity).activity.activity.feedId == (newItem as ActivityType.Activity).activity.activity.feedId
                    false -> oldItem == newItem
                }

            override fun areContentsTheSame(oldItem: ActivityType, newItem: ActivityType): Boolean =
                oldItem == newItem
        }
    }
}