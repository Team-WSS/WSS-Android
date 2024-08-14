package com.teamwss.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.teamwss.websoso.R
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
        binding.popularNovel = popularNovel
        with(binding) {
            ivPopularNovelNovelCover.load(popularNovel.novelImage)
            when (popularNovel.avatarImage.isNullOrEmpty()) {
                true -> ivPopularNovelAvatar.load(R.drawable.ic_home_alert)
                false -> ivPopularNovelAvatar.load(popularNovel.avatarImage)
            }
        }
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