package com.teamwss.websoso.ui.withDraw

import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWithDrawFirstBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class WithDrawFirstActivity :
    BindingActivity<ActivityWithDrawFirstBinding>(R.layout.activity_with_draw_first) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }
}