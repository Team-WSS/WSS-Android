package com.teamwss.websoso.ui.blockUsers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemBlockUserBinding
import com.teamwss.websoso.ui.blockUsers.model.BlockUsersModel.BlockUserModel

class BlockUsersViewHolder(
    private val binding: ItemBlockUserBinding,
    onRemoveBlockUserClick: (blockId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onRemoveBlockUserClick
    }

    fun bind(blockUser: BlockUserModel) {
        binding.blockUser = blockUser
    }

    companion object {

        fun of(
            parent: ViewGroup,
            removeBlockUserClick: (blockId: Long) -> Unit,
        ): BlockUsersViewHolder {
            val binding = ItemBlockUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
            return BlockUsersViewHolder(binding, removeBlockUserClick)
        }
    }
}