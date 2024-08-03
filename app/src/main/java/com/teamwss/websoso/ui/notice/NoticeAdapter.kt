package com.teamwss.websoso.ui.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNoticeBinding
import com.teamwss.websoso.ui.notice.model.Notice

class NoticeAdapter : ListAdapter<Notice, NoticeViewHolder>(NoticeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val NoticeDiffCallback = object : DiffUtil.ItemCallback<Notice>() {
            override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
                return oldItem.noticeTitle == newItem.noticeTitle
            }

            override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean {
                return oldItem == newItem
            }
        }
    }
}
