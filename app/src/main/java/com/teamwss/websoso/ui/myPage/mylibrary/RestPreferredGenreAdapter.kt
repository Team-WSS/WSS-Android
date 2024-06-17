package com.teamwss.websoso.ui.myPage.mylibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.teamwss.websoso.data.model.GenrePreferredEntity
import com.teamwss.websoso.databinding.ItemRestPreferredGenreBinding

class RestPreferredGenreAdapter(
    context: Context,
    resource: Int,
    items: List<GenrePreferredEntity>
) : ArrayAdapter<GenrePreferredEntity>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemRestPreferredGenreBinding
        val view: View

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            binding = ItemRestPreferredGenreBinding.inflate(layoutInflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemRestPreferredGenreBinding
        }

        val genre = getItem(position)
        if (genre != null) {
            binding.restPreferredGenre = genre
            binding.executePendingBindings()
        }
        return view
    }
}