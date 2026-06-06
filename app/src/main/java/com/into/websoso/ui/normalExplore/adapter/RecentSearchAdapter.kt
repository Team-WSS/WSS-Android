package com.into.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemNormalExploreRecentSearchBinding
import com.into.websoso.ui.normalExplore.model.NormalExploreModel.RecentSearchModel

class RecentSearchAdapter(
    private val recentSearchClickListener: (keyword: String) -> Unit,
    private val recentSearchDeleteClickListener: (recentSearchId: Long) -> Unit,
) : ListAdapter<RecentSearchModel, RecentSearchAdapter.RecentSearchViewHolder>(diffCallBack) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecentSearchViewHolder =
        RecentSearchViewHolder.from(
            parent = parent,
            recentSearchClickListener = recentSearchClickListener,
            recentSearchDeleteClickListener = recentSearchDeleteClickListener,
        )

    override fun onBindViewHolder(
        holder: RecentSearchViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class RecentSearchViewHolder(
        private val binding: ItemNormalExploreRecentSearchBinding,
        private val recentSearchClickListener: (keyword: String) -> Unit,
        private val recentSearchDeleteClickListener: (recentSearchId: Long) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recentSearch: RecentSearchModel) {
            binding.recentSearch = recentSearch
            binding.root.setOnClickListener {
                recentSearchClickListener(recentSearch.keyword)
            }
            binding.ivNormalExploreRecentSearchDelete.setOnClickListener {
                recentSearchDeleteClickListener(recentSearch.id)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                recentSearchClickListener: (keyword: String) -> Unit,
                recentSearchDeleteClickListener: (recentSearchId: Long) -> Unit,
            ): RecentSearchViewHolder =
                RecentSearchViewHolder(
                    ItemNormalExploreRecentSearchBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                    recentSearchClickListener,
                    recentSearchDeleteClickListener,
                )
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<RecentSearchModel>() {
            override fun areItemsTheSame(
                oldItem: RecentSearchModel,
                newItem: RecentSearchModel,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: RecentSearchModel,
                newItem: RecentSearchModel,
            ): Boolean = oldItem == newItem
        }
    }
}

