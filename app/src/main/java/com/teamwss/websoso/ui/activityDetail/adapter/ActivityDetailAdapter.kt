package com.teamwss.websoso.ui.activityDetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel

class ActivityDetailAdapter :
    ListAdapter<ActivityModel, ActivityDetailViewHolder>(diffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).feedId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityDetailViewHolder {
        return ActivityDetailViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ActivityDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ActivityModel>() {
            override fun areItemsTheSame(
                oldItem: ActivityModel,
                newItem: ActivityModel,
            ): Boolean = oldItem.feedId == newItem.feedId

            override fun areContentsTheSame(
                oldItem: ActivityModel,
                newItem: ActivityModel,
            ): Boolean = oldItem == newItem
        }
    }
}
