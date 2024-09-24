package com.teamwss.websoso.ui.storage

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.teamwss.websoso.R
import com.teamwss.websoso.common.util.toFloatPxFromDp
import com.teamwss.websoso.common.util.toIntPxFromDp
import com.teamwss.websoso.databinding.MenuStoragePopupBinding
import com.teamwss.websoso.ui.storage.model.SortType

class SortClickHandler(
    private val viewModel: StorageViewModel,
) : StorageSortTypeClickListener {

    @SuppressLint("InflateParams")
    override fun onSortButtonClick(view: View) {
        val inflater = LayoutInflater.from(view.context)
        val binding: MenuStoragePopupBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.menu_storage_popup,
            null,
            false,
        )

        binding.storageViewModel = viewModel

        val popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true,
        ).apply {
            elevation = POPUP_ELEVATION.toFloatPxFromDp()

            showAsDropDown(
                view,
                POPUP_MARGIN_END.toIntPxFromDp(),
                POPUP_MARGIN_TOP.toIntPxFromDp(),
                Gravity.END,
            )
        }

        binding.btnStorageNewest.setOnClickListener {
            viewModel.updateSortType(SortType.NEWEST)
            popupWindow.dismiss()
        }

        binding.btnStorageOldest.setOnClickListener {
            viewModel.updateSortType(SortType.OLDEST)
            popupWindow.dismiss()
        }
    }

    companion object {
        const val POPUP_MARGIN_END = -124
        const val POPUP_MARGIN_TOP = 20
        const val POPUP_ELEVATION = 14f
    }
}