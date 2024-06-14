package com.teamwss.websoso.ui.myPage.mylibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.teamwss.websoso.data.model.GenrePreferredData
import com.teamwss.websoso.databinding.ItemPreferredGenreBottomBinding

class GenreBottomAdapter(
    context: Context,
    resource: Int,
    items: List<GenrePreferredData.GenreBottom>
) : ArrayAdapter<GenrePreferredData.GenreBottom>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemPreferredGenreBottomBinding
        val view: View

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            binding = ItemPreferredGenreBottomBinding.inflate(layoutInflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemPreferredGenreBottomBinding
        }

        val genre = getItem(position)
        if (genre != null) {
            binding.genreBottom = genre
            binding.executePendingBindings()
        }
        return view
    }
}