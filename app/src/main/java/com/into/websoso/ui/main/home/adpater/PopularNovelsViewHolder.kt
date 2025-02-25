package com.into.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.data.model.PopularNovelsEntity.PopularNovelEntity
import com.into.websoso.databinding.ItemPopularNovelBinding

class PopularNovelsViewHolder(
    private val binding: ItemPopularNovelBinding,
    onPopularNovelClick: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = onPopularNovelClick
    }

    fun bind(popularNovel: PopularNovelEntity) {
        val nickname: String = popularNovel.nickname ?: "작품 설명"
        val avatarImage = itemView.getS3ImageUrl(popularNovel.avatarImage ?: "")
        if (popularNovel.avatarImage.isNullOrEmpty()) {
            with(binding) {
                ivPopularNovelAvatar.visibility = View.INVISIBLE
                tvPopularNovelInShortTitle.visibility = View.INVISIBLE
                ivPopularNovelAvatarNull.visibility = View.VISIBLE
                tvPopularNovelInShortTitleNull.visibility = View.VISIBLE
            }
        }
        val updatedPopularNovel = popularNovel.copy(
            nickname = nickname,
            avatarImage = avatarImage,
        )
        binding.popularNovel = updatedPopularNovel
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onPopularNovelClick: (novelId: Long) -> Unit,
        ): PopularNovelsViewHolder {
            val binding = ItemPopularNovelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return PopularNovelsViewHolder(binding, onPopularNovelClick)
        }
    }
}
