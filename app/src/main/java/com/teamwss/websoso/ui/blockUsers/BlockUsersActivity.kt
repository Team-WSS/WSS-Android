package com.teamwss.websoso.ui.blockUsers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityBlockUsersBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class BlockUsersActivity :
    BindingActivity<ActivityBlockUsersBinding>(R.layout.activity_block_users) {

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
        binding.ivBlockUsersBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, BlockUsersActivity::class.java)
        }
    }
}