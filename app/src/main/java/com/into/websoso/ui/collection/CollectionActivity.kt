package com.into.websoso.ui.collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.collection.CollectionNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WebsosoTheme {
                CollectionNavHost(onNavigateBack = ::finish)
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, CollectionActivity::class.java)
    }
}
