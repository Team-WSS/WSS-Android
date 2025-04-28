package com.into.websoso.ui.blockedUsers.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.into.websoso.data.model.BlockedUsersEntity.BlockedUserEntity

class BlockedUsersAdapter(
    private val onRemoveBlockedUserClick: (blockId: Long) -> (Unit),
) : ListAdapter<BlockedUserEntity, BlockedUsersViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BlockedUsersViewHolder = BlockedUsersViewHolder.of(parent, onRemoveBlockedUserClick)

    override fun onBindViewHolder(
        holder: BlockedUsersViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BlockedUserEntity>() {
            override fun areItemsTheSame(
                oldItem: BlockedUserEntity,
                newItem: BlockedUserEntity,
            ): Boolean = oldItem.blockId == newItem.blockId

            override fun areContentsTheSame(
                oldItem: BlockedUserEntity,
                newItem: BlockedUserEntity,
            ): Boolean = oldItem == newItem
        }
    }
}
