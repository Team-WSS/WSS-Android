package com.teamwss.websoso.ui.otherUserPage.otherUserLibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.databinding.ItemRestGenreBinding

class RestGenrePreferenceAdapter(
    items: List<GenrePreferenceEntity> = emptyList(),
) : BaseAdapter() {

    private var items: List<GenrePreferenceEntity> = items

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemRestGenreBinding = if (convertView == null) {
            ItemRestGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
                it.root.tag = it
            }
        } else {
            convertView.tag as ItemRestGenreBinding
        }

        val updateGenrePreference: GenrePreferenceEntity = getItem(position).copy(
            genreImage = parent.getS3ImageUrl(getItem(position).genreImage),
        )

        binding.genrePreference = updateGenrePreference
        binding.genrePreference = getItem(position)
        binding.executePendingBindings()
        return binding.root
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

    fun updateRestGenrePreferenceData(newItems: List<GenrePreferenceEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}