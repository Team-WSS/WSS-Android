package com.teamwss.websoso.ui.storage.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.StorageEntity
import com.teamwss.websoso.databinding.ItemStorageBinding

class StorageViewPagerViewHolder(
    private val binding: ItemStorageBinding,
    private val navigateToExplore: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(novels: List<StorageEntity.UserNovel>) {
        binding.hasData = novels.isNotEmpty()

        if (novels.isNotEmpty()) {
            val adapter = StorageItemAdapter(novels)
            binding.rvStorage.adapter = adapter
            binding.rvStorage.layoutManager = GridLayoutManager(binding.root.context, 3)
        }

        binding.btnStorageGoToSearchNovel.setOnClickListener {
            navigateToExplore()  // 전달받은 함수를 호출하여 탐색 프래그먼트로 이동
        }
    }
}


