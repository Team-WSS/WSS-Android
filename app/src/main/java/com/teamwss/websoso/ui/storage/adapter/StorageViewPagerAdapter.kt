package com.teamwss.websoso.ui.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.StorageEntity
import com.teamwss.websoso.databinding.ItemStorageBinding
import com.teamwss.websoso.ui.storage.model.StorageTab

class StorageViewPagerAdapter(
    private var novels: List<StorageEntity.StorageNovelEntity>,
    private val navigateToExplore: () -> Unit,
    private val novelClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<StorageViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewPagerViewHolder {
        val binding = ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StorageViewPagerViewHolder(binding, navigateToExplore, novelClickListener)
    }

    override fun onBindViewHolder(holder: StorageViewPagerViewHolder, position: Int) {
        holder.bind(novels)
    }

    override fun getItemCount(): Int {
        return StorageTab.values().size
    }

    fun updateNovels(newNovels: List<StorageEntity.StorageNovelEntity>) {
        this.novels = newNovels
        notifyDataSetChanged()
    }
}