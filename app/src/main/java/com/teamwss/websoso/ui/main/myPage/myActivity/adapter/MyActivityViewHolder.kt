package com.teamwss.websoso.ui.main.myPage.myActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel

class MyActivityViewHolder(
    private val binding: ItemMyActivityBinding,
    activityItemClickListener: ActivityItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = activityItemClickListener
    }

    fun bind(myActivity: ActivityModel, userProfile: UserProfileModel) {
        binding.activity = myActivity
        binding.userProfile = userProfile.copy(
            avatarImage = itemView.getS3ImageUrl(userProfile.avatarImage)
        )
        binding.clMyActivityLike.isSelected = myActivity.isLiked
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