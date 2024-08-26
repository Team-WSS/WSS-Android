package com.teamwss.websoso.ui.myPage.myActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.MyActivitiesEntity
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.myPage.myActivity.MyActivityViewModel

class MyActivityAdapter(private val myActivityVwModel: MyActivityViewModel) :
    ListAdapter<MyActivitiesEntity.MyActivityEntity, MyActivityViewHolder>(diffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).feedId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyActivityViewHolder {
        val binding = ItemMyActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyActivityViewHolder, position: Int) {
        val activity = getItem(position)
        val genreText = myActivityVwModel.translateGenres(activity.relevantCategories)
        holder.bind(activity, genreText,myActivityVwModel)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MyActivitiesEntity.MyActivityEntity>() {
            override fun areItemsTheSame(
                oldItem: MyActivitiesEntity.MyActivityEntity,
                newItem: MyActivitiesEntity.MyActivityEntity
            ): Boolean = oldItem.feedId == newItem.feedId

            override fun areContentsTheSame(
                oldItem: MyActivitiesEntity.MyActivityEntity,
                newItem: MyActivitiesEntity.MyActivityEntity
            ): Boolean = oldItem == newItem
        }
    }
}
