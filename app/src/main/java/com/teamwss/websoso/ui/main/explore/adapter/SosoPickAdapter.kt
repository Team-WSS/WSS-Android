package com.teamwss.websoso.ui.main.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemSosoPickBinding
import com.teamwss.websoso.ui.main.explore.model.SosoPickModel

class SosoPickAdapter(
    private val sosoPickItemClickListener: (Long) -> Unit,
) : ListAdapter<SosoPickModel, SosoPickViewHolder>(SosoPickDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SosoPickViewHolder {
        val binding =
            ItemSosoPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SosoPickViewHolder(binding, sosoPickItemClickListener)
    }

    override fun onBindViewHolder(holder: SosoPickViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}

class SosoPickDiffCallback : DiffUtil.ItemCallback<SosoPickModel>() {
    override fun areItemsTheSame(oldItem: SosoPickModel, newItem: SosoPickModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SosoPickModel, newItem: SosoPickModel): Boolean {
        return oldItem == newItem
    }
}