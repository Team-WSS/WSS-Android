package com.into.websoso.ui.blockedUsers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.data.model.BlockedUsersEntity.BlockedUserEntity
import com.into.websoso.databinding.ItemBlockedUserBinding

class BlockedUsersViewHolder(
    private val binding: ItemBlockedUserBinding,
    onRemoveBlockedUserClick: (blockId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = onRemoveBlockedUserClick
    }

    fun bind(blockedUser: BlockedUserEntity) {
        val imageUrl: String = itemView.getS3ImageUrl(blockedUser.avatarImage)

        val updatedCategory = blockedUser.copy(
            avatarImage = imageUrl,
        )

        binding.blockedUser = updatedCategory
    }

    companion object {
        fun of(
            parent: ViewGroup,
            removeBlockUserClick: (blockId: Long) -> Unit,
        ): BlockedUsersViewHolder {
            val binding = ItemBlockedUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return BlockedUsersViewHolder(binding, removeBlockUserClick)
        }
    }
}
