package com.teamwss.websoso.ui.myPage.myLibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.databinding.ItemRestGenreBinding

class RestGenrePreferenceAdapter(
    var items: List<GenrePreferenceEntity>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemRestGenreBinding
        val view: View

        if (convertView == null) {
            binding = ItemRestGenreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemRestGenreBinding
            view = convertView
        }

        binding.genrePreference = getItem(position)
        binding.executePendingBindings()
        return view
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): GenrePreferenceEntity {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}