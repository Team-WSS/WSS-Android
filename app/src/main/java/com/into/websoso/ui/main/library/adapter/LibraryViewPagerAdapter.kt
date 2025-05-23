package com.into.websoso.ui.main.library.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.databinding.ItemStorageBinding
import com.into.websoso.ui.userStorage.model.StorageTab
import kotlinx.coroutines.flow.Flow

class LibraryViewPagerAdapter(
    private val test: Flow<PagingData<NovelEntity>>,
    private val scope: LifecycleOwner,
    private val novelClickListener: (novelId: Long) -> Unit,
) : RecyclerView.Adapter<LibraryViewPagerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LibraryViewPagerViewHolder {
        val binding = ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryViewPagerViewHolder(binding, novelClickListener)
    }

    override fun onBindViewHolder(
        holder: LibraryViewPagerViewHolder,
        position: Int,
    ) {
        holder.bind(test, scope)
    }

    override fun getItemCount(): Int = StorageTab.entries.size
}
