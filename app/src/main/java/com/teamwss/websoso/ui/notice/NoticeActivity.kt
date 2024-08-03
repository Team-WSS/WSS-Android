package com.teamwss.websoso.ui.notice

import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNoticeBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : BindingActivity<ActivityNoticeBinding>(R.layout.activity_notice) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}