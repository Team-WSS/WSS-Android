package com.into.websoso.ui.userStorage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.into.websoso.R
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.library.LibraryScreen
import com.into.websoso.ui.main.MainActivity.FragmentType.EXPLORE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserStorageActivity : AppCompatActivity(R.layout.activity_storage) {
    @Inject
    lateinit var websosoNavigator: NavigatorProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            WebsosoTheme {
                LibraryScreen(
                    navigateToMainActivity = {
                        websosoNavigator.navigateToMainActivity(::startActivity, EXPLORE.name)
                    },
                    navigateToNovelDetailActivity = { novelId ->
                        websosoNavigator.navigateToNovelDetailActivity(novelId, ::startActivity)
                    },
                )
            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, UserStorageActivity::class.java)
    }
}
