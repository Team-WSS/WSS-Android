package com.into.websoso.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.IntegerRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.into.websoso.R.id.fcv_main
import com.into.websoso.R.id.menu_explore
import com.into.websoso.R.id.menu_feed
import com.into.websoso.R.id.menu_home
import com.into.websoso.R.id.menu_library
import com.into.websoso.R.id.menu_my_page
import com.into.websoso.R.layout.activity_main
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.resource.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.core.resource.R.string.main_back_press
import com.into.websoso.databinding.ActivityMainBinding
import com.into.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.into.websoso.ui.main.MainActivity.FragmentType.EXPLORE
import com.into.websoso.ui.main.MainActivity.FragmentType.FEED
import com.into.websoso.ui.main.MainActivity.FragmentType.HOME
import com.into.websoso.ui.main.MainActivity.FragmentType.LIBRARY
import com.into.websoso.ui.main.MainActivity.FragmentType.MY_PAGE
import com.into.websoso.ui.main.explore.ExploreFragment
import com.into.websoso.ui.main.feed.FeedFragment
import com.into.websoso.ui.main.home.HomeFragment
import com.into.websoso.ui.main.library.LibraryFragment
import com.into.websoso.ui.main.myPage.MyPageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(activity_main) {
    private val mainViewModel: MainViewModel by viewModels()
    private var backPressedTime: Long = 0L
    private var currentFragment: Fragment? = null

    private val fragmentTags = mapOf(
        menu_home to HomeFragment.TAG,
        menu_explore to ExploreFragment.TAG,
        menu_feed to FeedFragment.TAG,
        menu_library to LibraryFragment.TAG,
        menu_my_page to MyPageFragment.TAG,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupBackButtonListener()
        setupBottomNavigationView()
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
                        message = getString(main_back_press),
                        icon = ic_blocked_user_snack_bar,
                    )
                }

                false -> {
                    this.isEnabled = false
                    finish()
                }
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun setupBottomNavigationView() {
        setupInitialFragment()
        setupBottomNavListener()
        binding.bnvMain.selectedItemId = menu_home
    }

    private fun setupInitialFragment() {
        val initialItemId = menu_home
        val initialTag = fragmentTags[initialItemId]!!

        val existingFragment = supportFragmentManager.findFragmentByTag(initialTag)
        val fragment = existingFragment ?: findOrCreateFragment(initialTag)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            if (existingFragment == null) {
                add(fcv_main, fragment, initialTag)
            } else {
                show(existingFragment)
            }
        }

        currentFragment = fragment
    }

    private fun setupBottomNavListener() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            fragmentTags[item.itemId] ?: return@setOnItemSelectedListener true
            val currentFragment = supportFragmentManager.findFragmentById(fcv_main)

            if (item.itemId == menu_library && currentFragment is LibraryFragment) {
                supportFragmentManager.setFragmentResult("scrollToTop", Bundle.EMPTY)
            } else {
                replaceCurrentFragment(item.itemId)
            }

            true
        }
    }

    private fun replaceCurrentFragment(itemId: Int) {
        val tag = fragmentTags[itemId] ?: return
        val isLibrary = itemId == menu_library
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)
        val targetFragment = existingFragment ?: findOrCreateFragment(tag)

        supportFragmentManager.commit {
            setReorderingAllowed(true)

            currentFragment?.let {
                when {
                    it is LibraryFragment && !isLibrary -> hide(it)
                    it != targetFragment -> remove(it)
                    else -> { // 아무 것도 하지 않음. 나도 아무 것도 안하고 싶다...격하게
                    }
                }
            }

            if (existingFragment == null) {
                add(fcv_main, targetFragment, tag)
            } else {
                show(targetFragment)
            }
        }

        currentFragment = targetFragment
    }

    private fun findOrCreateFragment(tag: String): Fragment =
        supportFragmentManager.findFragmentByTag(tag) ?: when (tag) {
            HomeFragment.TAG -> HomeFragment()
            ExploreFragment.TAG -> ExploreFragment()
            FeedFragment.TAG -> FeedFragment()
            LibraryFragment.TAG -> LibraryFragment()
            MyPageFragment.TAG -> MyPageFragment()
            else -> throw IllegalArgumentException("Unknown fragment tag: $tag")
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

    private fun showLoginRequestDialog() {
        val dialog = LoginRequestDialogFragment.newInstance()
        dialog.show(supportFragmentManager, LoginRequestDialogFragment.TAG)
    }

    private fun handleNavigation(destination: FragmentType?) {
        val menuId = when (destination) {
            EXPLORE -> menu_explore
            MY_PAGE -> menu_my_page
            FEED -> menu_feed
            LIBRARY -> menu_library
            HOME, null -> menu_home
        }

        binding.bnvMain.selectedItemId = menuId
        replaceCurrentFragment(menuId)
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

    enum class FragmentType(
        @IntegerRes val resId: Int,
    ) {
        LIBRARY(menu_library),
        HOME(menu_home),
        EXPLORE(menu_explore),
        FEED(menu_feed),
        MY_PAGE(menu_my_page),
        ;

        companion object {
            fun valueOf(id: Int): FragmentType =
                entries.find { it.resId == id }
                    ?: throw IllegalArgumentException()

            fun valueOf(fragmentName: String): FragmentType =
                entries.find { it.name == fragmentName }
                    ?: throw IllegalArgumentException()
        }
    }
}
