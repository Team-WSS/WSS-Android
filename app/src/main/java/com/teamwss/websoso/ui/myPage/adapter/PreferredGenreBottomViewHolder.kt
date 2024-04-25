package com.teamwss.websoso.ui.myPage.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.teamwss.websoso.data.model.PreferredGenreData
import com.teamwss.websoso.databinding.ItemPreferredGenreBottomBinding

class PreferredGenreBottomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: ItemPreferredGenreBottomBinding =
        ItemPreferredGenreBottomBinding.bind(itemView)
    private val genreIcon: ImageView = binding.ivPreferredGenreBottom
    private val genreName: TextView = binding.tvPreferredGenreBottomName
    private val genreCount: TextView = binding.tvPreferredGenreBottomCount

    fun bindPreferredGenreBottom(preferredGenreBottom: PreferredGenreData.GenreBottom) {
        genreIcon.load(preferredGenreBottom.genreIcon)
        genreName.text = preferredGenreBottom.genreName
        genreCount.text = preferredGenreBottom.genreCount.toString()
    }
}