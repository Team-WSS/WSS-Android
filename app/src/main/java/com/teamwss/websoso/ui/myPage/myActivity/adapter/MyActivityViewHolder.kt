package com.teamwss.websoso.ui.myPage.myActivity.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.MyActivitiesEntity
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.myPage.myActivity.MyActivityViewModel

class MyActivityViewHolder(private val binding: ItemMyActivityBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(myActivity: MyActivitiesEntity.MyActivityEntity,genreText: String, myActivityViewModel: MyActivityViewModel) {
        binding.myActivity = myActivity
        binding.tvMyActivityCreatedDate.text = myActivityViewModel.formatDate(myActivity.createdDate)
        binding.tvMyActivityGenre.text = genreText
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