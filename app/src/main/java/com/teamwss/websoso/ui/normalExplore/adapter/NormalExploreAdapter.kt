package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.databinding.ItemNormalExploreBinding

class NormalExploreAdapter(
    private val novelItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<NormalExploreViewHolder>() {

    private var items: List<ExploreResultEntity.NovelEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalExploreViewHolder {
        val binding =
            ItemNormalExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalExploreViewHolder(binding, novelItemClickListener)
    }

    override fun onBindViewHolder(holder: NormalExploreViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateResultNovels(newItems: List<ExploreResultEntity.NovelEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}