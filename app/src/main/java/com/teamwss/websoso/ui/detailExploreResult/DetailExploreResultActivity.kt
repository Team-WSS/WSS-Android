package com.teamwss.websoso.ui.detailExploreResult

import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityDetailExploreResultBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class DetailExploreResultActivity :
    BindingActivity<ActivityDetailExploreResultBinding>(R.layout.activity_detail_explore_result) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}