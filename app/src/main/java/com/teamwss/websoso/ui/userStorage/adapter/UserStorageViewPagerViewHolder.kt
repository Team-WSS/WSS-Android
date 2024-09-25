package com.teamwss.websoso.ui.userStorage.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemStorageBinding
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageViewPagerViewHolder(
    private val binding: ItemStorageBinding,
    private val onExploreButtonClick: () -> Unit,
    private var novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(novels: List<StorageNovelModel>) {

        if (novels.isNotEmpty()) {
            val adapter = UserStorageItemAdapter(novels, novelClickListener)
            binding.rvStorage.adapter = adapter
            binding.rvStorage.layoutManager =
                GridLayoutManager(binding.root.context, STORAGE_NOVEL_SPAN_COUNT)
        }

        binding.btnStorageGoToSearchNovel.setOnClickListener {
            onExploreButtonClick()
        }
    }

    companion object {
        const val STORAGE_NOVEL_SPAN_COUNT = 3
    }
}