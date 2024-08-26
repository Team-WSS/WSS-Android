package com.teamwss.websoso.ui.myPage.myActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.myPage.myActivity.model.ActivityModel

class MyActivityViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(myActivity: ActivityModel) {
        binding.myActivity = myActivity
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): MyActivityViewHolder {
            val binding =
                ItemMyActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyActivityViewHolder(binding)
        }
    }
}