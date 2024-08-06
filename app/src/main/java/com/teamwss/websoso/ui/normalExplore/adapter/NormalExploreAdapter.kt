package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.NormalExploreEntity.NovelEntity
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreAdapter.ItemType.HEADER
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreAdapter.ItemType.RESULT

class NormalExploreAdapter(
    private val novelItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var novels: List<NovelEntity> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            HEADER_POSITION -> HEADER.ordinal
            else -> RESULT.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.valueOf(viewType)) {
            HEADER -> NormalExploreHeaderViewHolder.from(parent)
            RESULT -> NormalExploreViewHolder.of(parent, novelItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NormalExploreHeaderViewHolder -> holder.bind(novels.size)
            is NormalExploreViewHolder -> holder.bind(novels[position - HEADER_OFFSET])
        }
    }

    override fun getItemCount(): Int = novels.size + HEADER_ITEM_COUNT

    fun updateItems(newItems: List<NovelEntity>) {
        novels = newItems
        notifyDataSetChanged()
    }

    private enum class ItemType {
        HEADER, RESULT;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }

    companion object {
        private const val HEADER_POSITION = 0
        private const val HEADER_ITEM_COUNT = 1
        private const val HEADER_OFFSET = 1
    }
}