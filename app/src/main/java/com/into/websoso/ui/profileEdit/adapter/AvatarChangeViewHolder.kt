package com.into.websoso.ui.profileEdit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.common.util.getS3ImageUrl
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
        binding.root.tag = avatar.avatarId
        binding.avatar = avatar.copy(
            avatarThumbnail = binding.root.getS3ImageUrl(avatar.avatarThumbnail),
        )
        binding.isSelected = avatar.isRepresentative
    }

    fun updateItemSelection(isRepresentative: Boolean) {
        binding.isSelected = isRepresentative
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
