package com.teamwss.websoso.ui.myPage.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.teamwss.websoso.data.model.PreferredGenreData
import com.teamwss.websoso.databinding.ItemPreferredGenreTopBinding

class PreferredGenreTopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: ItemPreferredGenreTopBinding = ItemPreferredGenreTopBinding.bind(itemView)
    private val genreIcon: ImageView = binding.ivPreferredGenreTop
    private val genreName: TextView = binding.tvPreferredGenreTopName
    private val genreCount: TextView = binding.tvPreferredGenreTopCount

    fun bindPreferredGenreTop(preferredGenreTop: PreferredGenreData.GenreTop) {
        genreIcon.load(preferredGenreTop.genreIcon)
        genreName.text = preferredGenreTop.genreName
        genreCount.text = preferredGenreTop.genreCount.toString()
    }
}

