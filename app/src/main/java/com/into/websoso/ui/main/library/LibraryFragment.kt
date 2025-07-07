package com.into.websoso.ui.main.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.into.websoso.R
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.library.LibraryScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.cv_library)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WebsosoTheme {
                    LibraryScreen()
                }
            }
        }
        return view
    }
}

