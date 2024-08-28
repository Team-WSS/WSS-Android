package com.teamwss.websoso.ui.main.home.adpater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.data.model.UserInterestFeedsEntity.UserInterestFeedEntity

class UserInterestFeedAdapter(
    private val onUserInterestFeedClick: (novelId: Long) -> (Unit),
) : ListAdapter<UserInterestFeedEntity, UserInterestFeedViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInterestFeedViewHolder {
        return UserInterestFeedViewHolder.of(parent, onUserInterestFeedClick)
    }

    override fun onBindViewHolder(holder: UserInterestFeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<UserInterestFeedEntity>() {

            override fun areItemsTheSame(
                oldItem: UserInterestFeedEntity,
                newItem: UserInterestFeedEntity,
            ): Boolean {
                return oldItem.novelId == newItem.novelId
            }

            override fun areContentsTheSame(
                oldItem: UserInterestFeedEntity,
                newItem: UserInterestFeedEntity,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}