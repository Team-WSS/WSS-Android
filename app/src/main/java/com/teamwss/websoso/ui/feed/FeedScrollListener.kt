package com.teamwss.websoso.ui.feed

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.ui.feed.adapter.FeedAdapter
import com.teamwss.websoso.ui.feed.adapter.FeedType
import com.teamwss.websoso.util.SingleEventHandler

class FeedScrollListener private constructor(
    private val loadAdditionalFeeds: () -> Unit,
) : RecyclerView.OnScrollListener() {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if ((recyclerView.adapter as FeedAdapter).currentList.last() !is FeedType.Loading) return
        super.onScrollStateChanged(recyclerView, newState)

        val totalItemCount: Int = (recyclerView.adapter as FeedAdapter).itemCount
        val visibleLastItemPosition: Int =
            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

        if (visibleLastItemPosition in totalItemCount - 2..totalItemCount)
            singleEventHandler.handle(event = loadAdditionalFeeds)
    }

    companion object {

        fun from(event: () -> Unit): FeedScrollListener = FeedScrollListener(event)
    }
}
