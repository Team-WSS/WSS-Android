package com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel

class OtherUserActivityViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(activityModels: UserActivityModel) {
        val activity = activityModels.activity
        val userProfile = activityModels.userProfile

        binding.activity = activity
        binding.userProfile = userProfile.copy(
            avatarImage = itemView.getS3ImageUrl(userProfile.avatarImage)
        )
    }

    companion object {
        fun from(parent: ViewGroup): OtherUserActivityViewHolder {
            val binding =
                ItemMyActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return OtherUserActivityViewHolder(binding)
        }
    }
}