package com.teamwss.websoso.ui.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.StorageEntity
import com.teamwss.websoso.databinding.ItemStorageBinding
import com.teamwss.websoso.ui.storage.model.StorageTab

class StorageViewPagerAdapter(
    private val novelsMap: Map<StorageTab, List<StorageEntity.UserNovel>>,
    private val navigateToExplore: () -> Unit
) : RecyclerView.Adapter<StorageViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewPagerViewHolder {
        val binding =
            ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StorageViewPagerViewHolder(binding, navigateToExplore)
    }

    override fun onBindViewHolder(holder: StorageViewPagerViewHolder, position: Int) {
        val tab = StorageTab.fromPosition(position)
        holder.bind(novelsMap[tab] ?: emptyList())
    }

    override fun getItemCount(): Int {
        return 4
    }
}