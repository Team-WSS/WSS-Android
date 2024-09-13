package com.teamwss.websoso.ui.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.repository.FakeStorageRepository
import com.teamwss.websoso.databinding.ItemStorageBinding

class StorageViewPagerAdapter(
    repository: FakeStorageRepository,
    private val navigateToExplore: () -> Unit
) : RecyclerView.Adapter<StorageViewPagerViewHolder>() {

    private val interestNovels = repository.getInterestNovels()
    private val watchingNovels = repository.getWatchingNovels()
    private val watchedNovels = repository.getWatchedNovels()
    private val quittingNovels = repository.getQuittingNovels()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewPagerViewHolder {
        val binding =
            ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StorageViewPagerViewHolder(binding, navigateToExplore)
    }

    override fun onBindViewHolder(holder: StorageViewPagerViewHolder, position: Int) {
        when (position) {
            0 -> holder.bind(interestNovels)
            1 -> holder.bind(watchingNovels)
            2 -> holder.bind(watchedNovels)
            3 -> holder.bind(quittingNovels)
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}


