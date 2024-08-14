package com.teamwss.websoso.ui.blockedUsers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemBlockedUserBinding
import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersModel.BlockedUserModel

class BlockedUsersViewHolder(
    private val binding: ItemBlockedUserBinding,
    onRemoveBlockedUserClick: (blockId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onRemoveBlockedUserClick
    }

    fun bind(blockedUser: BlockedUserModel) {
        binding.blockedUser = blockedUser
    }

    companion object {

        fun of(
            parent: ViewGroup,
            removeBlockUserClick: (blockId: Long) -> Unit,
        ): BlockedUsersViewHolder {
            val binding = ItemBlockedUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
            return BlockedUsersViewHolder(binding, removeBlockUserClick)
        }
    }
}