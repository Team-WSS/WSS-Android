package com.into.websoso.ui.profileEdit.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.into.websoso.ui.profileEdit.model.AvatarModel

class AvatarPagerAdapter(
    private val onAvatarClick: (AvatarModel) -> Unit,
) : ListAdapter<List<AvatarModel>, AvatarSelectPageViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AvatarSelectPageViewHolder = AvatarSelectPageViewHolder.of(parent, onAvatarClick)

    override fun onBindViewHolder(
        holder: AvatarSelectPageViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: AvatarSelectPageViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            holder.bind(getItem(position))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<List<AvatarModel>>() {
            override fun areItemsTheSame(
                oldItem: List<AvatarModel>,
                newItem: List<AvatarModel>,
            ): Boolean =
                if (oldItem.isNotEmpty() && newItem.isNotEmpty()) {
                    oldItem.first().avatarId == newItem.first().avatarId
                } else {
                    oldItem === newItem
                }

            override fun areContentsTheSame(
                oldItem: List<AvatarModel>,
                newItem: List<AvatarModel>,
            ): Boolean {
                if (oldItem.size != newItem.size) return false
                return oldItem.zip(newItem).all { (old, new) ->
                    old.avatarId == new.avatarId && old.isRepresentative == new.isRepresentative
                }
            }

            override fun getChangePayload(
                oldItem: List<AvatarModel>,
                newItem: List<AvatarModel>,
            ): Any = Unit
        }
    }
}
