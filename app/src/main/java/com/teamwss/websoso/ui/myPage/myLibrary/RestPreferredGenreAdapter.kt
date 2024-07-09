package com.teamwss.websoso.ui.myPage.myLibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.teamwss.websoso.data.model.PreferredGenreEntity
import com.teamwss.websoso.databinding.ItemRestPreferredGenreBinding

class RestPreferredGenreAdapter(
    var items: List<PreferredGenreEntity>
) : BaseAdapter() {

    private var genreCount: String = ""

    fun updateGenreCount(genreCount: String) {
        this.genreCount = genreCount
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemRestPreferredGenreBinding
        val view: View

        if (convertView == null) {
            binding = ItemRestPreferredGenreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemRestPreferredGenreBinding
            view = convertView
        }

        binding.restPreferredGenre = getItem(position)
        return view
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): PreferredGenreEntity {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}

