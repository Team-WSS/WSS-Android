package com.teamwss.websoso.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamwss.websoso.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_detail)
    }

    companion object {
        private const val FEED_ID = "FEED_ID"

        fun from(
            id: Int,
            context: Context
        ): Intent = Intent(context, FeedDetailActivity::class.java).apply {
            putExtra(FEED_ID, id)
        }
    }
}
