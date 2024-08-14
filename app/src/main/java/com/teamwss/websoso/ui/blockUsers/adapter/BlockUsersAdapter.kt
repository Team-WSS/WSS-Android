package com.teamwss.websoso.ui.blockUsers.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.ui.blockUsers.model.BlockUsersModel.BlockUserModel

class BlockUsersAdapter(
    private val onRemoveBlockUserClick: (blockId: Long) -> (Unit),
) : ListAdapter<BlockUserModel, BlockUsersViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockUsersViewHolder {
        return BlockUsersViewHolder.of(parent, onRemoveBlockUserClick)
    }

    override fun onBindViewHolder(holder: BlockUsersViewHolder, position: Int) {
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