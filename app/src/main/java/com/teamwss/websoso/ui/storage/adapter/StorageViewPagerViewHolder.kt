package com.teamwss.websoso.ui.storage.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.StorageEntity
import com.teamwss.websoso.databinding.ItemStorageBinding

class StorageViewPagerViewHolder(
    private val binding: ItemStorageBinding,
    private val navigateToExplore: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(novels: List<StorageEntity.StorageNovelEntity>) {
        binding.hasData = novels.isNotEmpty()

        if (novels.isNotEmpty()) {
            val adapter = StorageItemAdapter(novels)
            binding.rvStorage.adapter = adapter
            binding.rvStorage.layoutManager = GridLayoutManager(binding.root.context, STORAGE_NOVEL_SPAN_COUNT)
        }

        binding.btnStorageGoToSearchNovel.setOnClickListener {
            navigateToExplore()
        }
    }

    companion object {
        const val STORAGE_NOVEL_SPAN_COUNT = 3
    }
}