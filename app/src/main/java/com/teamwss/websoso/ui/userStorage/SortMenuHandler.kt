package com.teamwss.websoso.ui.userStorage

import android.view.View
import com.teamwss.websoso.ui.userStorage.model.SortType

class SortMenuHandler(
    private val onSortTypeSelected: (SortType) -> Unit,
) {
    private val sortPopupHandler: SortPopupHandler by lazy {
        SortPopupHandler()
    }

    fun showSortMenu(view: View) {
        sortPopupHandler.showPopupMenu(view, onSortTypeSelected)
    }
}