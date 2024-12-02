package com.into.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.common.util.getS3ImageUrl
import com.into.websoso.data.model.UserInterestFeedsEntity.UserInterestFeedEntity
import com.into.websoso.databinding.ItemUserInterestFeedBinding

class UserInterestFeedViewHolder(
    private val binding: ItemUserInterestFeedBinding,
    onUserInterestFeedClick: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onUserInterestFeedClick
    }

    fun bind(userInterestFeed: UserInterestFeedEntity) {
        val updateUserInterestFeed = userInterestFeed.copy(
            avatarImage = itemView.getS3ImageUrl(userInterestFeed.avatarImage ?: ""),
        )
        binding.userInterestFeed = updateUserInterestFeed
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