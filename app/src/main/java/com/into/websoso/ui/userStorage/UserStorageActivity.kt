package com.into.websoso.ui.userStorage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.into.websoso.R
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.library.LibraryScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserStorageActivity : AppCompatActivity(R.layout.activity_storage) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            WebsosoTheme {
                LibraryScreen()
            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, UserStorageActivity::class.java)
    }
}
