package com.teamwss.websoso.ui.profileDisclosure

import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityProfileDisclosureBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class ProfileDisclosureActivity :
    BindingActivity<ActivityProfileDisclosureBinding>(R.layout.activity_profile_disclosure) {

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