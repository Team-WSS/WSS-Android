package com.teamwss.websoso.ui.profileEdit.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel

class AvatarChangeAdapter(
    private val onAvatarClick: (avatar: AvatarModel) -> (Unit),
) : ListAdapter<AvatarModel, AvatarChangeViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AvatarChangeViewHolder {
        return AvatarChangeViewHolder.of(parent, onAvatarClick)
    }

    override fun onBindViewHolder(holder: AvatarChangeViewHolder, position: Int) {
        if (holder.itemView.tag == null) {
            holder.setupItem(getItem(position))
        } else {
            holder.updateItemSelection(getItem(position).isRepresentative)
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<AvatarModel>() {

            override fun areItemsTheSame(
                oldItem: AvatarModel,
                newItem: AvatarModel,
            ): Boolean {
                return oldItem.avatarId == newItem.avatarId
            }

            override fun areContentsTheSame(
                oldItem: AvatarModel,
                newItem: AvatarModel,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
