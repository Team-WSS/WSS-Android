package com.teamwss.websoso.ui.userStorage

import android.view.View
import com.teamwss.websoso.ui.userStorage.model.SortType

class SortMenuHandler(
    private val onSortTypeSelected: (SortType) -> Unit,
) {

    fun showSortMenu(view: View) {
        val sortPopupHandler = SortPopupHandler()
        sortPopupHandler.showPopupMenu(view, onSortTypeSelected)
    }
}