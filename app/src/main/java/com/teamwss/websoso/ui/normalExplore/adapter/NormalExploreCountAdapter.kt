package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemNormalExploreResultCountBinding

class NormalExploreCountAdapter : RecyclerView.Adapter<NormalExploreCountViewHolder>() {
    private var item: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NormalExploreCountViewHolder {
        val binding =
            ItemNormalExploreResultCountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return NormalExploreCountViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: NormalExploreCountViewHolder, position: Int) {
        holder.bind(item)
    }

    fun updateResultNovels(novelSize: Int) {
        item = novelSize
        notifyDataSetChanged()
    }
}