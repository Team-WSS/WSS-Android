package com.teamwss.websoso.ui.feed

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedScrollListener private constructor(
    private val event: () -> Unit,
) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        // 보여지는 뷰에 한해서 마지막 아이템의 포지션을 나타낸다.
        val observedLastPosition: Int =
            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        // 현재 어댑터가 가지고 있는 아이템의 총 갯수이다.
        val totalItemCount: Int =
            (recyclerView.adapter?.itemCount ?: throw IllegalArgumentException()) - 1

        if (observedLastPosition == totalItemCount) {
            // 로딩뷰 적
            event()
        }

        // 디바운스 필요
        // 아예 아이템이 안오면 더 이상 로드할 데이터가 없다는 문구도 띄워야한다.
    }

    companion object {

        fun from(
            event: () -> Unit,
        ): FeedScrollListener = FeedScrollListener(event)
    }
}
