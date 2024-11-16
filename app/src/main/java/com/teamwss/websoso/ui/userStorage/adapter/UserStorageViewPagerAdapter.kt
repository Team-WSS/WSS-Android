package com.teamwss.websoso.ui.userStorage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.ItemStorageBinding
import com.teamwss.websoso.ui.userStorage.model.StorageTab
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class UserStorageViewPagerAdapter(
    private val novelClickListener: (novelId: Long) -> Unit,
    private val loadMoreCallback: () -> Unit,
    private val singleEventHandler: SingleEventHandler,
) : RecyclerView.Adapter<UserStorageViewPagerViewHolder>() {

    private val tabDataMap: MutableMap<Int, List<StorageNovelModel>> = mutableMapOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserStorageViewPagerViewHolder {
        val binding = ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserStorageViewPagerViewHolder(
            binding,
            novelClickListener,
            loadMoreCallback,
            singleEventHandler,
        )
    }

    override fun onBindViewHolder(holder: UserStorageViewPagerViewHolder, position: Int) {
        val novels = tabDataMap[position].orEmpty()
        holder.bind(novels)
    }

    override fun getItemCount(): Int = StorageTab.values().size

    fun updateNovels(position: Int, newNovels: List<StorageNovelModel>) {
        tabDataMap[position] = newNovels
        notifyItemChanged(position)
    }
}
