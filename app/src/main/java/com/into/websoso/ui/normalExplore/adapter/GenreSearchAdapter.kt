package com.into.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemNormalExploreGenreSearchBinding
import com.into.websoso.ui.detailExplore.info.model.Genre
import com.into.websoso.ui.normalExplore.model.GenreSearchModel

class GenreSearchAdapter(
    private val genreClickListener: (genre: Genre) -> Unit,
) : ListAdapter<GenreSearchModel, GenreSearchAdapter.GenreSearchViewHolder>(diffCallBack) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GenreSearchViewHolder =
        GenreSearchViewHolder.from(
            parent = parent,
            genreClickListener = genreClickListener,
        )

    override fun onBindViewHolder(
        holder: GenreSearchViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class GenreSearchViewHolder(
        private val binding: ItemNormalExploreGenreSearchBinding,
        private val genreClickListener: (genre: Genre) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genreSearch: GenreSearchModel) {
            binding.genreSearch = genreSearch
            binding.ivNormalExploreGenreSearchIcon.setImageResource(genreSearch.drawableRes)
            binding.root.setOnClickListener {
                genreClickListener(genreSearch.genre)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                genreClickListener: (genre: Genre) -> Unit,
            ): GenreSearchViewHolder =
                GenreSearchViewHolder(
                    ItemNormalExploreGenreSearchBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                    genreClickListener,
                )
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<GenreSearchModel>() {
            override fun areItemsTheSame(
                oldItem: GenreSearchModel,
                newItem: GenreSearchModel,
            ): Boolean = oldItem.genre == newItem.genre

            override fun areContentsTheSame(
                oldItem: GenreSearchModel,
                newItem: GenreSearchModel,
            ): Boolean = oldItem == newItem
        }
    }
}
