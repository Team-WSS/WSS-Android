package com.into.websoso.ui.expandedFeedImage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.core.designsystem.theme.WebsosoTheme

class ExpandedFeedImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebsosoTheme {
                ExpandedFeedImageScreen(
                    index = intent.getIntExtra(FEED_IMAGE_INDEX, 0),
                    imageUrls = intent.getStringArrayListExtra(FEED_IMAGE_URLS)
                        ?: return@WebsosoTheme,
                    onBackButtonClick = {
                        setResult(ResultFrom.Notification.RESULT_OK)
                        finish()
                    },
                )
            }
        }
    }

    companion object {
        private const val FEED_IMAGE_URLS = "FEED_IMAGE_URLS"
        private const val FEED_IMAGE_INDEX = "FEED_IMAGE_INDEX"

        fun getIntent(
            context: Context,
            index: Int,
            imageUrls: List<String>,
        ): Intent =
            Intent(context, ExpandedFeedImageActivity::class.java).apply {
                putExtra(FEED_IMAGE_INDEX, index)
                putStringArrayListExtra(FEED_IMAGE_URLS, ArrayList(imageUrls))
            }
    }
}
