package com.into.websoso.ui.collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.collection.CollectionNavHost
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WebsosoTheme {
                CollectionNavHost(
                    onNavigateBack = ::finish,
                    onNovelClick = { startActivity(NovelDetailActivity.getIntent(this, it)) },
                    initialCollectionId = intent.getLongExtra(COLLECTION_ID, 0L).takeIf { it > 0L },
                    userId = intent.getLongExtra(USER_ID, 0L).takeIf { it > 0L },
                )
            }
        }
    }

    companion object {
        private const val COLLECTION_ID = "COLLECTION_ID"
        private const val USER_ID = "USER_ID"

        fun getIntent(
            context: Context,
            collectionId: Long? = null,
            userId: Long? = null,
        ): Intent =
            Intent(context, CollectionActivity::class.java).apply {
                collectionId?.let { putExtra(COLLECTION_ID, it) }
                userId?.let { putExtra(USER_ID, it) }
            }
    }
}
