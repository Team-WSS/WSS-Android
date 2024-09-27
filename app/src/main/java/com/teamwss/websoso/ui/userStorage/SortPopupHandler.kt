package com.teamwss.websoso.ui.userStorage

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.teamwss.websoso.common.util.toFloatPxFromDp
import com.teamwss.websoso.common.util.toIntPxFromDp
import com.teamwss.websoso.databinding.MenuStoragePopupBinding
import com.teamwss.websoso.ui.userStorage.model.SortType

class SortPopupHandler {

    fun showPopupMenu(
        view: View,
        onSortTypeSelected: (SortType) -> Unit,
    ) {
        val inflater = LayoutInflater.from(view.context)
        val binding: MenuStoragePopupBinding = MenuStoragePopupBinding.inflate(inflater)

        val popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true,
        ).apply {
            isTouchable = true
            isFocusable = true
            isOutsideTouchable = true
            elevation = POPUP_ELEVATION.toFloatPxFromDp()

            showAsDropDown(
                view,
                POPUP_MARGIN_END.toIntPxFromDp(),
                POPUP_MARGIN_TOP.toIntPxFromDp(),
                Gravity.END,
            )
        }

        binding.btnStorageNewest.setOnClickListener {
            onSortTypeSelected(SortType.NEWEST)
            popupWindow.dismiss()
        }

        binding.btnStorageOldest.setOnClickListener {
            onSortTypeSelected(SortType.OLDEST)
            popupWindow.dismiss()
        }
    }

    companion object {
        const val POPUP_MARGIN_END = -124
        const val POPUP_MARGIN_TOP = 20
        const val POPUP_ELEVATION = 14f
    }
}