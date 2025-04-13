package com.into.websoso.ui.main.library.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemStorageBinding
import com.into.websoso.ui.userStorage.model.StorageTab
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class LibraryViewPagerAdapter(
    private var novels: List<StorageNovelModel>,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<LibraryViewPagerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LibraryViewPagerViewHolder {
        val binding = ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryViewPagerViewHolder(binding, novelClickListener)
    }

    override fun onBindViewHolder(
        holder: LibraryViewPagerViewHolder,
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
