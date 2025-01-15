package com.into.websoso.ui.notice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : ComponentActivity() {

    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NoticeScreen(viewModel = noticeViewModel)
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NoticeActivity::class.java)
        }
    }
}
