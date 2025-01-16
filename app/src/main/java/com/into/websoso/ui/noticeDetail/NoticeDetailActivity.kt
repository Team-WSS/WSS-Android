package com.into.websoso.ui.noticeDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.activity.addCallback
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseActivity
import com.into.websoso.databinding.ActivityNoticeDetailBinding

class NoticeDetailActivity : BaseActivity<ActivityNoticeDetailBinding>(R.layout.activity_notice_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val noticeItem = intent.getSerializableExtra(NOTICE_DETAIL_KEY) as? NoticeModel
//        binding.noticeItem = noticeItem

        onBackButtonClick()
        handleBackPressed()
//        setupHyperlink(noticeItem?.noticeContent)
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

    private fun setupHyperlink(noticeContent: String?) {
        binding.tvNoticeDetailContent.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = noticeContent?.let { content ->
                Html.fromHtml(
                    content,
                    Html.FROM_HTML_MODE_LEGACY,
                )
            }
        }
    }

    companion object {
        const val NOTICE_DETAIL_KEY = "NOTICE_DETAIL_KEY"

        fun getIntent(
            context: Context,
            noticeId: Long,
        ): Intent =
            Intent(context, NoticeDetailActivity::class.java).apply {
                putExtra(NOTICE_DETAIL_KEY, noticeId)
            }
    }
}
