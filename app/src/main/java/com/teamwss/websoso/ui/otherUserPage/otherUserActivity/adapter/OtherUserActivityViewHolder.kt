package com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityModel

class OtherUserActivityViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(myActivity: ActivityModel) {
        binding.myActivity = myActivity
    }

    companion object {
        fun from(parent: ViewGroup): OtherUserActivityViewHolder {
            val binding =
                ItemMyActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return OtherUserActivityViewHolder(binding)
        }
    }
}