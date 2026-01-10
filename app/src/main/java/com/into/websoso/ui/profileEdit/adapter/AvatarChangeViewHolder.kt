package com.into.websoso.ui.profileEdit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.databinding.ItemProfileEditAvatarBinding
import com.into.websoso.ui.profileEdit.model.AvatarModel

class AvatarChangeViewHolder(
    private val binding: ItemProfileEditAvatarBinding,
    onAvatarClick: (avatar: AvatarModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onAvatarClick = onAvatarClick
    }

    fun setupItem(avatar: AvatarModel) {
        binding.avatar = avatar.copy(
            avatarProfile = binding.root.getS3ImageUrl(avatar.avatarProfile),
        )
        binding.isSelected = avatar.isRepresentative

        binding.executePendingBindings()
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onAvatarClick: (avatar: AvatarModel) -> Unit,
        ): AvatarChangeViewHolder {
            val binding = ItemProfileEditAvatarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return AvatarChangeViewHolder(binding, onAvatarClick)
        }
    }
}
