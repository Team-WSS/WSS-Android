package com.teamwss.websoso.ui.profileEdit.adapter

import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemProfileEditAvatarBinding
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel

class AvatarChangeViewHolder(
    private val binding: ItemProfileEditAvatarBinding,
    private val onAvatarClick: (avatar: AvatarModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(avatar: AvatarModel) {
        binding.imageUrl = avatar.avatarThumbnail
        binding.isSelected = avatar.isRepresentative

        binding.root.setOnClickListener {
            onAvatarClick(avatar)
        }
    }
}
