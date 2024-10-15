package com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.teamwss.websoso.ui.main.myPage.myActivity.adapter.MyActivityViewHolder
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel

class OtherUserActivityViewHolder(
    private val binding: ItemMyActivityBinding,
    activityItemClickListener: ActivityItemClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = activityItemClickListener
    }

    fun bind(activityModels: UserActivityModel) {
        val activity = activityModels.activity
        val userProfile = activityModels.userProfile

        binding.activity = activity
        binding.userProfile = userProfile.copy(
            avatarImage = itemView.getS3ImageUrl(userProfile.avatarImage)
        )
        binding.clMyActivityLike.isSelected = activity.isLiked
    }

    companion object {
        fun from(parent: ViewGroup, onClick: ActivityItemClickListener): MyActivityViewHolder =
            MyActivityViewHolder(
                ItemMyActivityBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                onClick,
            )
    }
}