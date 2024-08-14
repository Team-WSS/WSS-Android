package com.teamwss.websoso.ui.blockedUsers.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersModel.BlockUserModel

class BlockedUsersAdapter(
    private val onRemoveBlockUserClick: (blockId: Long) -> (Unit),
) : ListAdapter<BlockUserModel, BlockedUsersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUsersViewHolder {
        return BlockedUsersViewHolder.of(parent, onRemoveBlockUserClick)
    }

    override fun onBindViewHolder(holder: BlockedUsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BlockUserModel>() {

            override fun areItemsTheSame(
                oldItem: BlockUserModel,
                newItem: BlockUserModel
            ): Boolean {
                return oldItem.blockId == newItem.blockId
            }

            override fun areContentsTheSame(
                oldItem: BlockUserModel,
                newItem: BlockUserModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}