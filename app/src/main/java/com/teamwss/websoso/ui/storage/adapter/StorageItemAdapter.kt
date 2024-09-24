package com.teamwss.websoso.ui.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.StorageEntity
import com.teamwss.websoso.databinding.ItemStorageNovelBinding

class StorageItemAdapter(
    private val novels: List<StorageEntity.StorageNovelEntity>,
    private val novelClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<StorageItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageItemViewHolder {
        val binding =
            ItemStorageNovelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StorageItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StorageItemViewHolder, position: Int) {
        val novel = novels[position]
        holder.bind(novel)
        holder.itemView.setOnClickListener {
            novelClickListener(novel.novelId)
        }
    }

    override fun getItemCount(): Int {
        return novels.size
    }
}