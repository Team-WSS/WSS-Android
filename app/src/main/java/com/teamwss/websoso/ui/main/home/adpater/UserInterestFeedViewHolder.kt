package com.teamwss.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.UserInterestFeedsEntity.UserInterestFeedEntity
import com.teamwss.websoso.databinding.ItemUserInterestFeedBinding

class UserInterestFeedViewHolder(
    private val binding: ItemUserInterestFeedBinding,
    onUserInterestFeedClick: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onUserInterestFeedClick
    }

    fun bind(userInterestFeed: UserInterestFeedEntity) {
        binding.userInterestFeed = userInterestFeed
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onUserInterestFeedClick: (novelId: Long) -> Unit,
        ): UserInterestFeedViewHolder {
            val binding = ItemUserInterestFeedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
            return UserInterestFeedViewHolder(binding, onUserInterestFeedClick)
        }
    }
}