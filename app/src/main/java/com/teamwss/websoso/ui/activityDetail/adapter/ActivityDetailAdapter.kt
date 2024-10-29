package com.teamwss.websoso.ui.activityDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel

class ActivityDetailAdapter :
    ListAdapter<UserActivityModel, ActivityDetailViewHolder>(diffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).activity.feedId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityDetailViewHolder {
        val binding =
            ItemMyActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityDetailViewHolder, position: Int) {
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
