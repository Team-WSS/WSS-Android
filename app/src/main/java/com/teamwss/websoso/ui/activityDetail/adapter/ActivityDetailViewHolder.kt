package com.teamwss.websoso.ui.activityDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel

class ActivityDetailViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(activity: ActivityModel) {
        binding.activity = activity
        binding.executePendingBindings()
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
