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

    override fun getItemCount(): Int = ITEM_NOVEL_COUNT_AREA

    override fun onBindViewHolder(holder: NormalExploreCountViewHolder, position: Int) {
        holder.bind(item)
    }

    fun updateResultNovels(novelSize: Int) {
        item = novelSize
        notifyDataSetChanged()
    }

    companion object {
        private const val ITEM_NOVEL_COUNT_AREA = 1
    }
}