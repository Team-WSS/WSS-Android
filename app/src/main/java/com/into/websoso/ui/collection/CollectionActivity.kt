package com.into.websoso.ui.collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R
import com.into.websoso.feature.collection.CollectionNavHost
import com.into.websoso.feature.collection.model.CollectionShareContent
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.kakao.sdk.share.ShareClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionActivity : ComponentActivity() {
    private var isSharing by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WebsosoTheme {
                CollectionNavHost(
                    onNavigateBack = ::finish,
                    onNovelClick = { startActivity(NovelDetailActivity.getIntent(this, it)) },
                    onShare = ::shareCollection,
                    onShareBlocked = ::logShareBlocked,
                    isSharing = isSharing,
                    initialCollectionId = intent.getLongExtra(COLLECTION_ID, 0L).takeIf { it > 0L },
                    userId = intent.getLongExtra(USER_ID, 0L).takeIf { it > 0L },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isSharing = false
    }

    private fun shareCollection(content: CollectionShareContent) {
        if (isSharing) return
        if (!ShareClient.instance.isKakaoTalkSharingAvailable(this)) {
            Toast.makeText(this, R.string.collection_share_kakao_required, Toast.LENGTH_SHORT).show()
            return
        }
        isSharing = true
        runCatching {
            ShareClient.instance.shareCustom(
                context = this,
                templateId = CollectionKakaoShare.templateId(content),
                templateArgs = CollectionKakaoShare.templateArgs(content),
            ) { result, error ->
                isSharing = false
                if (isFinishing || isDestroyed) return@shareCustom
                if (error != null || result == null) {
                    showShareError()
                } else {
                    runCatching { startActivity(result.intent) }.onFailure { showShareError() }
                }
            }
        }.onFailure {
            isSharing = false
            showShareError()
        }
    }

    private fun showShareError() {
        Toast.makeText(this, R.string.collection_share_failed, Toast.LENGTH_SHORT).show()
    }

    private fun logShareBlocked(
        collectionId: Long,
        novelCount: Int,
        novelsSize: Int,
        isPublic: Boolean,
    ) {
        Firebase.analytics.logEvent("collection_share_blocked") {
            param("collection_id", collectionId)
            param("novel_count", novelCount.toLong())
            param("novels_size", novelsSize.toLong())
            param("is_public", if (isPublic) 1L else 0L)
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
