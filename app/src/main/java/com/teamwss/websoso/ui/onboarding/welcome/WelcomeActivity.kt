package com.teamwss.websoso.ui.onboarding.welcome

import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWelcomeBinding
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.ui.main.MainActivity

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(R.layout.activity_welcome) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCompleteButtonClick()
    }

    private fun onCompleteButtonClick() {
        binding.btnWelcomeStart.setOnClickListener {
            startActivity(MainActivity.getIntent(this))
        }
    }
}