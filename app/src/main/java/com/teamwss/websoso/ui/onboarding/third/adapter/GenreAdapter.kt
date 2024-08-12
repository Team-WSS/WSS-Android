package com.teamwss.websoso.ui.onboarding.third.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ItemOnboardingGenreBinding
import com.teamwss.websoso.ui.onboarding.third.model.Genre

class GenreAdapter(
    private val genres: List<Genre>,
    private val onGenreToggle: (String) -> Unit,
    private val isGenreSelected: (String) -> Boolean,
) : RecyclerView.Adapter<GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemOnboardingGenreBinding>(
            inflater, R.layout.item_onboarding_genre, parent, false
        )
        return GenreViewHolder(binding, onGenreToggle)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.bind(genre, isGenreSelected(genre.tag))
    }

    override fun getItemCount() = genres.size
}
