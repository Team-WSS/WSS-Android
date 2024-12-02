package com.into.websoso.ui.notice.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.into.websoso.R
import com.into.websoso.databinding.ItemNoticeBinding
import com.into.websoso.ui.notice.model.NoticeModel

class NoticeViewHolder(
    private val binding: ItemNoticeBinding,
    onNoticeClick: (notice: NoticeModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onNoticeClick
    }

    fun bind(notice: NoticeModel) {
        binding.notice = notice

        // 1차 릴리즈에는 공지사항만 존재 하기 때문에 고정 이미지를 사용합니다.
        binding.ivItemNoticeIcon.load(R.drawable.ic_home_alert)
    }
}