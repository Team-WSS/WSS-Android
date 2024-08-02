package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemNormalExploreResultCountBinding

class NormalExploreHeaderAdapter : RecyclerView.Adapter<NormalExploreHeaderViewHolder>() {
    private var item: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NormalExploreHeaderViewHolder {
        val binding =
            ItemNormalExploreResultCountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return NormalExploreHeaderViewHolder(binding)
    }

    override fun getItemCount(): Int = ITEM_COUNT

    override fun onBindViewHolder(holder: NormalExploreHeaderViewHolder, position: Int) {
        holder.bind(item)
    }

    fun updateResultNovels(novelSize: Int) {
        item = novelSize
        notifyDataSetChanged()
    }

    companion object {
        private const val ITEM_COUNT = 1
    }
}