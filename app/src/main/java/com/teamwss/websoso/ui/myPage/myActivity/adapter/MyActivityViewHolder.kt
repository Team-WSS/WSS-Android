package com.teamwss.websoso.ui.myPage.myActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.MyActivitiesEntity
import com.teamwss.websoso.databinding.ItemMyActivityBinding

class MyActivityViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(myActivity: MyActivitiesEntity.MyActivityEntity) {
        binding.myActivity = myActivity
        binding.executePendingBindings()
    }

    companion object {
        fun of(parent: ViewGroup): MyActivityViewHolder {
            val binding =
                ItemMyActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyActivityViewHolder(binding)
        }
    }
}