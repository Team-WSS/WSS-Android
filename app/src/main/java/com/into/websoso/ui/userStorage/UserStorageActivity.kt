package com.into.websoso.ui.userStorage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.util.setupWhiteSystemBar
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.feature.library.LibraryScreen
import com.into.websoso.feature.library.LibraryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserStorageActivity : ComponentActivity() {
    @Inject
    lateinit var websosoNavigator: NavigatorProvider
    private val libraryViewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupWhiteSystemBar()
        setContent {
            WebsosoTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = White)
                        .windowInsetsPadding(WindowInsets.systemBars),
                ) { inner ->
                    Box(modifier = Modifier.padding(inner)) {
                        LibraryScreen(
                            libraryViewModel = libraryViewModel,
                            navigateToNormalExploreActivity = {
                                websosoNavigator.navigateToNormalExploreActivity(::startActivity)
                            },
                            navigateToNovelDetailActivity = { novelId ->
                                websosoNavigator.navigateToNovelDetailActivity(
                                    novelId,
                                    ::startActivity,
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val USER_ID = "USER_ID"

        fun getIntent(
            context: Context,
            userId: Long,
        ) = Intent(context, UserStorageActivity::class.java).apply {
            putExtra(USER_ID, userId)
        }
    }
}
