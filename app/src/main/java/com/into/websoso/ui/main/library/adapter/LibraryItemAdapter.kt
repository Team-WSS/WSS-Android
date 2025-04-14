package com.into.websoso.ui.main.library.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemStorageNovelBinding
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class LibraryItemAdapter(
    private var novels: List<StorageNovelModel>,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<LibraryItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LibraryItemViewHolder {
        val binding =
            ItemStorageNovelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryItemViewHolder(binding, novelClickListener)
    }

    override fun onBindViewHolder(
        holder: LibraryItemViewHolder,
        position: Int,
    ) {
        val novel = novels[position]
        holder.bind(novel)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNovels(newNovels: List<StorageNovelModel>) {
        this.novels = newNovels
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = novels.size
}
