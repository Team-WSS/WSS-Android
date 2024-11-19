package com.teamwss.websoso.ui.onboarding.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityWelcomeBinding
import com.teamwss.websoso.ui.main.MainActivity

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(R.layout.activity_welcome) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nickname = intent.getStringExtra(NICKNAME_KEY) ?: "웹소소"

        setWelcomeUserTextView(nickname)
        onCompleteButtonClick()
    }

    private fun setWelcomeUserTextView(nickname: String) {
        binding.tvWelcomeHelloUser.text = getString(R.string.welcome_hello_user, nickname)
    }

    private fun onCompleteButtonClick() {
        binding.btnWelcomeStart.setOnClickListener {
            startActivity(MainActivity.getIntent(this, true))
            finish()
        }
    }

    companion object {
        private const val NICKNAME_KEY = "nickname"

        fun getIntent(context: Context, nickname: String): Intent =
            Intent(context, WelcomeActivity::class.java).apply {
                putExtra(NICKNAME_KEY, nickname)
            }
    }
}
