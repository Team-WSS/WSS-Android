package com.into.websoso.ui.main.library.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.into.websoso.databinding.ItemStorageNovelBinding
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

class LibraryItemAdapter2(
    private val novelClickListener: (novelId: Long) -> Unit,
) : PagingDataAdapter<StorageNovelModel, LibraryItemViewHolder>(UIMODEL_COMPARATOR) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LibraryItemViewHolder {
        val binding =
            ItemStorageNovelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryItemViewHolder(binding, novelClickListener)
    }

    override fun onBindViewHolder(
        holder: LibraryItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position)!!)
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<StorageNovelModel>() {
            override fun areItemsTheSame(
                oldItem: StorageNovelModel,
                newItem: StorageNovelModel,
            ): Boolean = oldItem.novelId == newItem.novelId

            override fun areContentsTheSame(
                oldItem: StorageNovelModel,
                newItem: StorageNovelModel,
            ): Boolean = oldItem == newItem
        }
    }
}
