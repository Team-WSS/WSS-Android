package com.teamwss.websoso.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemSosoPickBinding

class SosoPickAdapter : RecyclerView.Adapter<SosoPickViewHolder>() {
    val sosoPickData: List<SosoPickData> = SosoPickData.sosoMockData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SosoPickViewHolder {
        val binding =
            ItemSosoPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SosoPickViewHolder(binding)
    }

    override fun getItemCount(): Int = sosoPickData.size

    override fun onBindViewHolder(holder: SosoPickViewHolder, position: Int) {
        holder.onBind(sosoPickData[position])
    }
}