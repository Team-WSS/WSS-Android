package com.into.websoso.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.databinding.ActivityMainBinding
import com.into.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.into.websoso.ui.main.MainActivity.FragmentType.EXPLORE
import com.into.websoso.ui.main.MainActivity.FragmentType.FEED
import com.into.websoso.ui.main.MainActivity.FragmentType.HOME
import com.into.websoso.ui.main.MainActivity.FragmentType.MY_PAGE
import com.into.websoso.ui.main.explore.ExploreFragment
import com.into.websoso.ui.main.feed.FeedFragment
import com.into.websoso.ui.main.home.HomeFragment
import com.into.websoso.ui.main.library.LibraryFragment
import com.into.websoso.ui.main.myPage.MyPageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel: MainViewModel by viewModels()
    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBackButtonListener()
        setBottomNavigationView()
        setupObserver()
        onViewGuestClick()
        handleNavigation(intent.getSerializableExtra(DESTINATION_KEY) as? FragmentType)
    }

    private fun setupBackButtonListener() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (System.currentTimeMillis() - backPressedTime > 2000) {
                true -> {
                    backPressedTime = System.currentTimeMillis()
                    showWebsosoSnackBar(
                        view = binding.root,
                        message = getString(R.string.main_back_press),
                        icon = R.drawable.ic_blocked_user_snack_bar,
                    )
                }

                false -> {
                    this.isEnabled = false
                    finish()
                }
            }
        }
    }

    private fun setBottomNavigationView() {
        binding.bnvMain.selectedItemId = R.id.menu_home
        replaceFragment<HomeFragment>()

        binding.bnvMain.setOnItemSelectedListener(::replaceFragment)
    }

    private fun setupObserver() {
        mainViewModel.isLogin.observe(this) { isLogin ->
            when (isLogin) {
                true -> {
                    binding.viewMainGuest.visibility = View.GONE
                    mainViewModel.updateUserInfo()
                }

                false -> binding.viewMainGuest.visibility = View.VISIBLE
            }
        }
    }

    private fun onViewGuestClick() {
        binding.viewMainGuest.setOnClickListener {
            showLoginRequestDialog()
        }
    }

    private fun replaceFragment(item: MenuItem): Boolean {
        when (FragmentType.valueOf(item.itemId)) {
            HOME -> replaceFragment<HomeFragment>()
            EXPLORE -> replaceFragment<ExploreFragment>()
            FEED -> replaceFragment<FeedFragment>()
            FragmentType.LIBRARY -> replaceFragment<LibraryFragment>()
            MY_PAGE -> replaceFragment<MyPageFragment>()
        }
        return true
    }

    private inline fun <reified T : Fragment> replaceFragment() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_main)
            setReorderingAllowed(true)
        }
    }

    enum class FragmentType(
        @IntegerRes private val resId: Int,
    ) {
        HOME(R.id.menu_home),
        EXPLORE(R.id.menu_explore),
        FEED(R.id.menu_feed),
        LIBRARY(R.id.menu_library),
        MY_PAGE(R.id.menu_my_page),
        ;

        companion object {
            fun valueOf(id: Int): FragmentType =
                entries.find { it.resId == id }
                    ?: throw IllegalArgumentException()
        }
    }

    private fun showLoginRequestDialog() {
        val dialog = LoginRequestDialogFragment.newInstance()
        dialog.show(supportFragmentManager, LoginRequestDialogFragment.TAG)
    }

    private fun handleNavigation(destination: FragmentType?) {
        when (destination) {
            EXPLORE -> selectFragment(EXPLORE)
            MY_PAGE -> selectFragment(MY_PAGE)
            FEED -> selectFragment(FEED)
            FragmentType.LIBRARY -> selectFragment(FragmentType.LIBRARY)
            HOME, null -> selectFragment(HOME)
        }
    }

    private fun selectFragment(fragmentType: FragmentType) {
        when (fragmentType) {
            HOME -> {
                binding.bnvMain.selectedItemId = R.id.menu_home
                replaceFragment<HomeFragment>()
            }

            EXPLORE -> {
                binding.bnvMain.selectedItemId = R.id.menu_explore
                replaceFragment<ExploreFragment>()
            }

            FEED -> {
                binding.bnvMain.selectedItemId = R.id.menu_feed
                replaceFragment<FeedFragment>()
            }

            FragmentType.LIBRARY -> {
                binding.bnvMain.selectedItemId = R.id.menu_library
                replaceFragment<LibraryFragment>()
            }

            MY_PAGE -> {
                binding.bnvMain.selectedItemId = R.id.menu_my_page
                replaceFragment<MyPageFragment>()
            }
        }
    }

    companion object {
        private const val DESTINATION_KEY = "destination"
        const val IS_LOGIN_KEY = "isLogin"

        fun getIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        fun getIntent(
            context: Context,
            destination: FragmentType = HOME,
        ): Intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(DESTINATION_KEY, destination)
            }

        fun getIntent(
            context: Context,
            isLogin: Boolean,
        ): Intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(IS_LOGIN_KEY, isLogin)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
    }
}
