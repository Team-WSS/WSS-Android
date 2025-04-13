package com.into.websoso.ui.userStorage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.databinding.ItemStorageNovelBinding
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageItemAdapter(
    private var novels: List<StorageNovelModel>,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<UserStorageItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UserStorageItemViewHolder {
        val binding =
            ItemStorageNovelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserStorageItemViewHolder(binding, novelClickListener)
    }

    override fun onBindViewHolder(
        holder: UserStorageItemViewHolder,
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
