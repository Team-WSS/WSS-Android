package com.teamwss.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.PopularNovelsEntity.PopularNovelEntity
import com.teamwss.websoso.databinding.ItemPopularNovelBinding

class PopularNovelsViewHolder(
    private val binding: ItemPopularNovelBinding,
    onPopularNovelClick: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onPopularNovelClick
    }

    fun bind(popularNovel: PopularNovelEntity) {
        val nickname = popularNovel.nickname ?: "작품 설명"
        var avatarImage = popularNovel.avatarImage
        if (popularNovel.avatarImage.isNullOrEmpty()) {
            avatarImage = ""
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
                LayoutInflater.from(parent.context), parent, false,
            )
            return PopularNovelsViewHolder(binding, onPopularNovelClick)
        }
    }
}