package com.into.websoso.ui.profileEdit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemAvatarPageBinding
import com.into.websoso.ui.profileEdit.model.AvatarModel

class AvatarSelectPageViewHolder(
    private val binding: ItemAvatarPageBinding,
    private val onAvatarClick: (selectedAvatar: AvatarModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(items: List<AvatarModel>) {
        val childAdapter = binding.rvAvatarPage.adapter as? AvatarChangeAdapter
            ?: AvatarChangeAdapter(onAvatarClick).also {
                binding.rvAvatarPage.adapter = it
                binding.rvAvatarPage.layoutManager = GridLayoutManager(binding.root.context, 5)
                binding.rvAvatarPage.itemAnimator = null
            }

        childAdapter.submitList(items.map { it.copy() })
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onAvatarClick: (avatar: AvatarModel) -> Unit,
        ): AvatarSelectPageViewHolder {
            val binding = ItemAvatarPageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return AvatarSelectPageViewHolder(binding, onAvatarClick)
        }
    }
}
