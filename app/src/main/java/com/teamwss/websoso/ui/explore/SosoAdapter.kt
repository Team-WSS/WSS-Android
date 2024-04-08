package com.teamwss.websoso.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemSosoPickBinding

class SosoAdapter : RecyclerView.Adapter<SosoViewHolder>() {
    val sosoData: List<SosoData> = SosoData.sosoMockData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SosoViewHolder {
        val binding =
            ItemSosoPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SosoViewHolder(binding)
    }

    override fun getItemCount(): Int = sosoData.size

    override fun onBindViewHolder(holder: SosoViewHolder, position: Int) {
        holder.onBind(sosoData[position])
    }
}