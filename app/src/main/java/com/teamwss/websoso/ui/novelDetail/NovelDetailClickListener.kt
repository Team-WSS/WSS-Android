package com.teamwss.websoso.ui.novelDetail

import android.view.View

interface NovelDetailClickListener {

    fun onNovelDetailPopupClick(view: View, tempForReportError: Int, userNovelId: Int)
}