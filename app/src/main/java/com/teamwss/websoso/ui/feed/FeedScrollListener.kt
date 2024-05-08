package com.teamwss.websoso.ui.feed

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.util.SingleEventHandler

class FeedScrollListener private constructor(
    private val event: () -> Unit,
) : RecyclerView.OnScrollListener() {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler() }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val visibleLastItemPosition: Int =
            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        val totalItemCount: Int =
            recyclerView.adapter?.itemCount ?: throw IllegalArgumentException()

        if (visibleLastItemPosition in totalItemCount - 2..totalItemCount)
            singleEventHandler.handle(event = event)
    }

    companion object {

        fun from(event: () -> Unit): FeedScrollListener = FeedScrollListener(event)
    }
}
