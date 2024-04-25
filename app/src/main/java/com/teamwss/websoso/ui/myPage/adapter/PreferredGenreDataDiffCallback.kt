package com.teamwss.websoso.ui.myPage.adapter

import androidx.recyclerview.widget.DiffUtil
import com.teamwss.websoso.data.model.PreferredGenreData

class PreferredGenreDataDiffCallback(
    private val oldList: List<PreferredGenreData>,
    private val newList: List<PreferredGenreData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
