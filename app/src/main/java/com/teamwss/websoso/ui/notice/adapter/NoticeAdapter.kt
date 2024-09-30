package com.teamwss.websoso.ui.notice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNoticeBinding
import com.teamwss.websoso.ui.notice.model.NoticeModel

class NoticeAdapter(
    private val onNoticeClick: (notice: NoticeModel) -> Unit,
) : ListAdapter<NoticeModel, NoticeViewHolder>(NoticeModelDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding, onNoticeClick)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val NoticeModelDiffCallback = object : DiffUtil.ItemCallback<NoticeModel>() {
            override fun areItemsTheSame(oldItem: NoticeModel, newItem: NoticeModel): Boolean {
                return oldItem.noticeTitle == newItem.noticeTitle
            }

            override fun areContentsTheSame(oldItem: NoticeModel, newItem: NoticeModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
