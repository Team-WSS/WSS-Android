package com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel

class OtherUserActivityAdapter :
    ListAdapter<UserActivityModel, OtherUserActivityViewHolder>(diffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).activity.feedId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherUserActivityViewHolder {
        return OtherUserActivityViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OtherUserActivityViewHolder, position: Int) {
        val userActivityModel = getItem(position)
        holder.bind(userActivityModel)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<UserActivityModel>() {
            override fun areItemsTheSame(
                oldItem: UserActivityModel,
                newItem: UserActivityModel,
            ): Boolean = oldItem.activity.feedId == newItem.activity.feedId

            override fun areContentsTheSame(
                oldItem: UserActivityModel,
                newItem: UserActivityModel,
            ): Boolean = oldItem == newItem
        }
    }
}