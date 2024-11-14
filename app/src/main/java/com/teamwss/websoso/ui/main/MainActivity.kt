package com.teamwss.websoso.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityMainBinding
import com.teamwss.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.EXPLORE
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.FEED
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.HOME
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.MY_PAGE
import com.teamwss.websoso.ui.main.explore.ExploreFragment
import com.teamwss.websoso.ui.main.feed.FeedFragment
import com.teamwss.websoso.ui.main.home.HomeFragment
import com.teamwss.websoso.ui.main.myPage.MyPageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLogin = intent.getBooleanExtra(IS_LOGIN_KEY, true)
        if (isLogin.not()) binding.viewMainGuest.visibility = View.VISIBLE
        mainViewModel.setIsLogin(isLogin)

        setBottomNavigationView()
        onViewGuestClick()
        handleNavigation(intent.getSerializableExtra(DESTINATION_KEY) as? FragmentType)
        if (isLogin) mainViewModel.updateUserInfo()
    }

    private fun setBottomNavigationView() {
        binding.bnvMain.selectedItemId = R.id.menu_home
        replaceFragment<HomeFragment>()

        binding.bnvMain.setOnItemSelectedListener(::replaceFragment)
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

    enum class FragmentType(@IntegerRes private val resId: Int) {
        HOME(R.id.menu_home),
        EXPLORE(R.id.menu_explore),
        FEED(R.id.menu_feed),
        MY_PAGE(R.id.menu_my_page)
        ;

        companion object {
            fun valueOf(id: Int): FragmentType = entries.find { fragmentView ->
                fragmentView.resId == id
            } ?: throw IllegalArgumentException()
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

            MY_PAGE -> {
                binding.bnvMain.selectedItemId = R.id.menu_my_page
                replaceFragment<MyPageFragment>()
            }
        }
    }

    companion object {
        private const val DESTINATION_KEY = "destination"
        private const val IS_LOGIN_KEY = "isLogin"

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }

        fun getIntent(
            context: Context,
            destination: FragmentType = HOME,
        ): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(DESTINATION_KEY, destination)
            }
        }

        fun getIntent(context: Context, isLogin: Boolean): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(IS_LOGIN_KEY, isLogin)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
