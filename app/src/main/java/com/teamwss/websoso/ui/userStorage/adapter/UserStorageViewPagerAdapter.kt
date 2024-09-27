package com.teamwss.websoso.ui.userStorage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemStorageBinding
import com.teamwss.websoso.ui.userStorage.model.StorageTab
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageViewPagerAdapter(
    private var novels: List<StorageNovelModel>,
    private val onExploreButtonClick: () -> Unit,
    private val novelClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<UserStorageViewPagerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserStorageViewPagerViewHolder {
        val binding = ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserStorageViewPagerViewHolder(binding, onExploreButtonClick, novelClickListener)
    }

    override fun onBindViewHolder(holder: UserStorageViewPagerViewHolder, position: Int) {
        holder.bind(novels)
    }

    override fun getItemCount(): Int {
        return StorageTab.values().size
    }

    fun updateNovels(newNovels: List<StorageNovelModel>) {
        this.novels = newNovels
        notifyDataSetChanged()
    }
}