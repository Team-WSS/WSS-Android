package com.teamwss.websoso.ui.main.home.adpater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.PopularNovelsEntity.PopularNovelEntity

class PopularNovelsAdapter(
    private val onPopularNovelClick: (novelId: Long) -> (Unit),
) : ListAdapter<PopularNovelEntity, PopularNovelsViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularNovelsViewHolder {
        return PopularNovelsViewHolder.of(parent, onPopularNovelClick)
    }

    override fun onBindViewHolder(holder: PopularNovelsViewHolder, position: Int) {
        holder.bind(getItem(position))

        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        params.width = (holder.itemView.context.resources.displayMetrics.widthPixels * 0.83).toInt()
        holder.itemView.layoutParams = params
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PopularNovelEntity>() {

            override fun areItemsTheSame(
                oldItem: PopularNovelEntity,
                newItem: PopularNovelEntity,
            ): Boolean {
                return oldItem.novelId == newItem.novelId
            }

            override fun areContentsTheSame(
                oldItem: PopularNovelEntity,
                newItem: PopularNovelEntity,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}