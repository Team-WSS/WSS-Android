package com.teamwss.websoso.ui.changeGenderAndAge

import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityChangeGenderAndAgeBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class ChangeGenderAndAgeActivity :
    BindingActivity<ActivityChangeGenderAndAgeBinding>(R.layout.activity_change_gender_and_age) {

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