package com.into.websoso.ui.onboarding.third.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.into.websoso.databinding.ItemOnboardingGenreBinding
import com.into.websoso.ui.onboarding.third.model.Genre

class GenreViewHolder(
    private val binding: ItemOnboardingGenreBinding,
    private val onGenreToggle: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        genre: Genre,
        isSelected: Boolean,
    ) {
        binding.genre = genre
        binding.isSelected = isSelected
        with(binding) {
            ivOnboardingItemGenreImage.load(genre.drawableRes)
            root.setOnClickListener {
                onGenreToggle(genre.tag)
            }
            executePendingBindings()
        }
    }
}
