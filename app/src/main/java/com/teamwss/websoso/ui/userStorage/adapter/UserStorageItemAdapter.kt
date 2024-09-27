package com.teamwss.websoso.ui.userStorage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemStorageNovelBinding
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageItemAdapter(
    private var novels: List<StorageNovelModel>,
    private val novelClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<UserStorageItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserStorageItemViewHolder {
        val binding =
            ItemStorageNovelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserStorageItemViewHolder(binding, novelClickListener)
    }

    override fun onBindViewHolder(holder: UserStorageItemViewHolder, position: Int) {
        val novel = novels[position]
        holder.bind(novel)
    }

    fun updateNovels(newNovels: List<StorageNovelModel>) {
        this.novels = newNovels
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return novels.size
    }
}