package com.into.websoso.ui.main.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.into.websoso.R
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.library.LibraryScreen
import com.into.websoso.feature.library.LibraryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    @Inject
    lateinit var websosoNavigator: NavigatorProvider
    private val libraryViewModel: LibraryViewModel by viewModels()

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
                    LibraryScreen(
                        libraryViewModel = libraryViewModel,
                        navigateToNormalExploreActivity = {
                            websosoNavigator.navigateToNormalExploreActivity(::startActivity)
                        },
                        navigateToNovelDetailActivity = { novelId ->
                            websosoNavigator.navigateToNovelDetailActivity(novelId, ::startActivity)
                        },
                    )
                }
            }
        }
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("scrollToTop", viewLifecycleOwner) { _, _ ->
            libraryViewModel.resetScrollPosition()
        }
    }

    companion object {
        const val TAG = "LibraryFragment"
    }
}
