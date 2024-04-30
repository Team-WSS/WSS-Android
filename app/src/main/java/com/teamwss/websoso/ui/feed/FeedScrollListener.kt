package com.teamwss.websoso.ui.feed

import androidx.recyclerview.widget.RecyclerView

class FeedScrollListener private constructor(
    private val recyclerview: RecyclerView,
    private val event: () -> Unit,
) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!recyclerview.canScrollVertically(DIRECTION_BOTTOM)
            && newState == RecyclerView.SCROLL_STATE_IDLE
        ) {
            event()
        } // 디바운스 필요
        // onScroll이랑 무슨 차이인지 찾아보기
    }

    companion object {
        private const val DIRECTION_BOTTOM = 1

        fun of(
            recyclerview: RecyclerView,
            event: () -> Unit,
        ): FeedScrollListener = FeedScrollListener(recyclerview, event)
    }
}
