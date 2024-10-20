package com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel

class OtherUserActivityAdapter :
    ListAdapter<ActivityModel, OtherUserActivityViewHolder>(diffCallback) {
    private var userProfile: UserProfileModel? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).feedId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherUserActivityViewHolder {
        return OtherUserActivityViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OtherUserActivityViewHolder, position: Int) {
        val activity = getItem(position)

        userProfile?.let { userProfile ->
            val activityModels = UserActivityModel(activity, userProfile)
            holder.bind(activityModels)
        }
    }

    fun setUserProfile(profile: UserProfileModel) {
        userProfile = profile
        if (currentList.isNotEmpty()) {
            notifyItemRangeChanged(0, currentList.size)
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