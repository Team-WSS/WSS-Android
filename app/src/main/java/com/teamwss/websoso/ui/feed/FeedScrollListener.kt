package com.teamwss.websoso.ui.feed

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedScrollListener private constructor(
    private val event: () -> Unit,
) : RecyclerView.OnScrollListener() {
    private var label: Int = 0

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val visibleLastItemPosition: Int =
            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        val totalItemCount: Int =
            recyclerView.adapter?.itemCount ?: throw IllegalArgumentException()

        if (visibleLastItemPosition in totalItemCount - 2..totalItemCount) {
            when (label) {
                0 -> return
                1 -> event()
            }
        }
    }

    companion object {
        private const val READY = 0
        private const val PROGRESS = 1

        fun from(event: () -> Unit): FeedScrollListener = FeedScrollListener(event)
    }
}
