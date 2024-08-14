package com.teamwss.websoso.ui.blockedUsers.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.data.model.BlockedUsersEntity.*

class BlockedUsersAdapter(
    private val onRemoveBlockedUserClick: (blockId: Long) -> (Unit),
) : ListAdapter<BlockedUserEntity, BlockedUsersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUsersViewHolder {
        return BlockedUsersViewHolder.of(parent, onRemoveBlockedUserClick)
    }

    override fun onBindViewHolder(holder: BlockedUsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BlockedUserEntity>() {

            override fun areItemsTheSame(
                oldItem: BlockedUserEntity,
                newItem: BlockedUserEntity
            ): Boolean {
                return oldItem.blockId == newItem.blockId
            }

            override fun areContentsTheSame(
                oldItem: BlockedUserEntity,
                newItem: BlockedUserEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}