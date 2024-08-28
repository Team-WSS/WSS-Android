package com.teamwss.websoso.common.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener private constructor(
    private val singleEventHandler: SingleEventHandler,
    private val loadAdditionalFeeds: () -> Unit,
) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        // if ((recyclerView.adapter as FeedAdapter).currentList.last() !is FeedType.Loading) return
        super.onScrollStateChanged(recyclerView, newState)

        val totalItemCount: Int = recyclerView.adapter!!.itemCount
        val visibleLastItemPosition: Int =
            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

        if (visibleLastItemPosition in totalItemCount - 2..totalItemCount)
            singleEventHandler.throttleFirst { loadAdditionalFeeds() }
    }

    companion object {

        fun of(
            singleEventHandler: SingleEventHandler,
            event: () -> Unit,
        ): InfiniteScrollListener = InfiniteScrollListener(singleEventHandler, event)
    }
}
