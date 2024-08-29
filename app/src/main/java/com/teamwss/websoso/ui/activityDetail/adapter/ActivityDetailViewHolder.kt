package com.teamwss.websoso.ui.activityDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityModel

class ActivityDetailViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(myActivity: ActivityModel) {
        binding.myActivity = myActivity
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
