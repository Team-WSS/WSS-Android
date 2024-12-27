package com.into.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.into.websoso.data.model.PopularFeedsEntity.PopularFeedEntity
import com.into.websoso.databinding.ItemPopularFeedBinding

class PopularFeedsAdapter(
    private val onFeedClick: (feedId: Long) -> Unit,
) : ListAdapter<List<PopularFeedEntity>, PopularFeedsViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularFeedsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPopularFeedBinding.inflate(inflater, parent, false)
        return PopularFeedsViewHolder(binding, onFeedClick)
    }

    override fun onBindViewHolder(holder: PopularFeedsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<List<PopularFeedEntity>>() {
            override fun areItemsTheSame(
                oldItem: List<PopularFeedEntity>,
                newItem: List<PopularFeedEntity>,
            ): Boolean {
                return oldItem.firstOrNull()?.feedId == newItem.firstOrNull()?.feedId
            }

            override fun areContentsTheSame(
                oldItem: List<PopularFeedEntity>,
                newItem: List<PopularFeedEntity>,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}