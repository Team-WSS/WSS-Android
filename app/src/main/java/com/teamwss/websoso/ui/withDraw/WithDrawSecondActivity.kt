package com.teamwss.websoso.ui.withDraw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWithDrawSecondBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class WithDrawSecondActivity :
    BindingActivity<ActivityWithDrawSecondBinding>(R.layout.activity_with_draw_second) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        onBackButtonClick()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun onBackButtonClick() {
        binding.ivWithDrawBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, WithDrawSecondActivity::class.java)
        }
    }
}