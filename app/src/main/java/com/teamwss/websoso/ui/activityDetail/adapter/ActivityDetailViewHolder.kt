package com.teamwss.websoso.ui.activityDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel

class ActivityDetailViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(activity: ActivityModel, userProfile: UserProfileModel) {
        binding.activity = activity
        binding.userProfile = userProfile.copy(
            avatarImage = itemView.getS3ImageUrl(userProfile.avatarImage)
        )
    }

    companion object {
        fun from(parent: ViewGroup): ActivityDetailViewHolder {
            val binding =
                ItemMyActivityBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ActivityDetailViewHolder(binding)
        }
    }
}