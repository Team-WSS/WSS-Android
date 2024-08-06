package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.NormalExploreEntity.NovelEntity

class NormalExploreAdapter(
    private val novelItemClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<NovelEntity> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_RESULT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.valueOf(viewType)) {
            ItemType.HEADER -> NormalExploreHeaderViewHolder.from(parent)
            ItemType.RESULT -> NormalExploreViewHolder.of(parent, novelItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NormalExploreHeaderViewHolder -> holder.bind(items.count())
            is NormalExploreViewHolder -> holder.onBind(items[position - 1])
        }
    }

    override fun getItemCount(): Int = items.size + 1

    fun updateItems(newItems: List<NovelEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    private enum class ItemType {
        HEADER, RESULT;

        companion object {

            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_RESULT = 1
    }
}