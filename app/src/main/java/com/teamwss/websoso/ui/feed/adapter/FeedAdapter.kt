package com.teamwss.websoso.ui.feed.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.ui.common.base.BaseDiffUtil
import com.teamwss.websoso.ui.feed.FeedItemClickListener

class FeedAdapter(
    private val onClick: FeedItemClickListener
) : ListAdapter<Feed, FeedViewHolder>(diffCallBack) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder =
        FeedViewHolder.from(parent, onClick)

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallBack = object : BaseDiffUtil<Feed>() {}
    }
}
