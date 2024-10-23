package com.teamwss.websoso.ui.main.myPage.myActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemMyActivityBinding
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel

class MyActivityAdapter(
    private val activityItemClickListener: ActivityItemClickListener
) : ListAdapter<ActivityModel, MyActivityViewHolder>(diffCallback) {
    var userProfile: UserProfileModel? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).feedId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyActivityViewHolder {
        val binding =
            ItemMyActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyActivityViewHolder(binding, activityItemClickListener)
    }

    override fun onBindViewHolder(holder: MyActivityViewHolder, position: Int) {
        val activity = getItem(position)

        userProfile?.let { userProfile ->
            val activityModels = UserActivityModel(activity, userProfile)
            holder.bind(activityModels)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ActivityModel>() {
            override fun areItemsTheSame(
                oldItem: ActivityModel,
                newItem: ActivityModel,
            ): Boolean = oldItem.feedId == newItem.feedId

            override fun areContentsTheSame(
                oldItem: ActivityModel,
                newItem: ActivityModel,
            ): Boolean = oldItem == newItem
        }
    }
}
