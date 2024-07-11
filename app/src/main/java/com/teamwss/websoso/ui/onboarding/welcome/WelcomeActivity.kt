package com.teamwss.websoso.ui.onboarding.welcome

import android.content.Intent
import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWelcomeBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.main.MainActivity

class WelcomeActivity : BindingActivity<ActivityWelcomeBinding>(R.layout.activity_welcome) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupOnCompleteButtonClick()
    }

    private fun setupOnCompleteButtonClick() {
        binding.btnWelcomeStart.setOnClickListener {
            val intent = MainActivity.getIntent(this)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}