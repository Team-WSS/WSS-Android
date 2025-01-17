package com.into.websoso.ui.activityDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.databinding.ItemMyActivityBinding
import com.into.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.into.websoso.ui.main.myPage.myActivity.model.UserActivityModel

class ActivityDetailViewHolder(
    private val binding: ItemMyActivityBinding,
    activityItemClickListener: ActivityItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = activityItemClickListener
    }

    fun bind(activityModels: UserActivityModel) {
        val activity = activityModels.activity
        val userProfile = activityModels.userProfile

        binding.activity = activity
        binding.userProfile = userProfile.copy(
            avatarImage = itemView.getS3ImageUrl(userProfile.avatarImage),
        )

        binding.clMyActivityLike.isSelected = activity.isLiked
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClick: ActivityItemClickListener,
        ): ActivityDetailViewHolder =
            ActivityDetailViewHolder(
                ItemMyActivityBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                onClick,
            )
    }
}
