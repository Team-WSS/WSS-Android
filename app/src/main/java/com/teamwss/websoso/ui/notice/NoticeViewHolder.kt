package com.teamwss.websoso.ui.notice

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ItemNoticeBinding
import com.teamwss.websoso.ui.notice.model.NoticeModel

class NoticeViewHolder(private val binding: ItemNoticeBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(noticeModel: NoticeModel) {

        binding.notice = noticeModel

        // 1차 릴리즈에는 공지사항만 존재 하기 때문에 고정 이미지를 사용합니다.
        binding.ivItemNoticeIcon.load(R.drawable.ic_home_alert)
    }
}
