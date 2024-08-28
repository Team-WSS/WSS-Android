package com.teamwss.websoso.ui.profileEdit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemProfileEditAvatarBinding
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel

class AvatarChangeAdapter(
    private val onAvatarClick: (avatar: AvatarModel) -> (Unit),
) : ListAdapter<AvatarModel, AvatarChangeViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AvatarChangeViewHolder {
        val binding = ItemProfileEditAvatarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return AvatarChangeViewHolder(binding, onAvatarClick)
    }

    override fun onBindViewHolder(holder: AvatarChangeViewHolder, position: Int) {
        val currentAvatar = getItem(position)
        if (holder.itemView.tag == null) {
            holder.bind(currentAvatar)
            holder.itemView.tag = currentAvatar.avatarId
        } else {
            holder.updateSelection(currentAvatar.isRepresentative)
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
