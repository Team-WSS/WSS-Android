package com.teamwss.websoso.ui.noticeDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityNoticeDetailBinding
import com.teamwss.websoso.ui.notice.model.NoticeModel

class NoticeDetailActivity :
    BaseActivity<ActivityNoticeDetailBinding>(R.layout.activity_notice_detail) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val noticeItem = intent.getSerializableExtra(NOTICE_DETAIL_KEY) as? NoticeModel
        binding.noticeItem = noticeItem

        onBackButtonClick()
        handleBackPressed()
    }

    private fun onBackButtonClick() {
        binding.ivNoticeDetailBack.setOnClickListener {
            finish()
        }
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    companion object {
        const val NOTICE_DETAIL_KEY = "NOTICE_ITEM_KEY"

        fun getIntent(context: Context, noticeModel: NoticeModel): Intent {
            return Intent(context, NoticeDetailActivity::class.java).apply {
                putExtra(NOTICE_DETAIL_KEY, noticeModel)
            }
        }
    }
}