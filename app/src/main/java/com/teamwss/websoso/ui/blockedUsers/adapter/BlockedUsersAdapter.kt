package com.teamwss.websoso.ui.blockedUsers.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersModel.BlockedUserModel

class BlockedUsersAdapter(
    private val onRemoveBlockedUserClick: (blockId: Long) -> (Unit),
) : ListAdapter<BlockedUserModel, BlockedUsersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUsersViewHolder {
        return BlockedUsersViewHolder.of(parent, onRemoveBlockedUserClick)
    }

    override fun onBindViewHolder(holder: BlockedUsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BlockedUserModel>() {

            override fun areItemsTheSame(
                oldItem: BlockedUserModel,
                newItem: BlockedUserModel
            ): Boolean {
                return oldItem.blockId == newItem.blockId
            }

            override fun areContentsTheSame(
                oldItem: BlockedUserModel,
                newItem: BlockedUserModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}