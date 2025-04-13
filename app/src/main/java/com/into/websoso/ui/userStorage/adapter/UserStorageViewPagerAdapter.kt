package com.into.websoso.ui.userStorage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemStorageBinding
import com.into.websoso.ui.userStorage.model.StorageTab
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageViewPagerAdapter(
    private var novels: List<StorageNovelModel>,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<UserStorageViewPagerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UserStorageViewPagerViewHolder {
        val binding = ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserStorageViewPagerViewHolder(binding, novelClickListener)
    }

    override fun onBindViewHolder(
        holder: UserStorageViewPagerViewHolder,
        position: Int,
    ) {
        holder.bind(novels)
    }

    override fun getItemCount(): Int = StorageTab.entries.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateNovels(newNovels: List<StorageNovelModel>) {
        this.novels = newNovels
        notifyDataSetChanged()
    }
}
