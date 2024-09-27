package com.teamwss.websoso.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
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

        mainViewModel.updateLogin(intent.getBooleanExtra(EXTRA_IS_LOGIN, false))

        setBottomNavigationView()
        handleExploreNavigation()
        mainViewModel.updateUserInfo()
    }

    private fun setBottomNavigationView() {
        binding.bnvMain.selectedItemId = R.id.menu_home
        replaceFragment<HomeFragment>()

        binding.bnvMain.setOnItemSelectedListener(::replaceFragment)

        val navigateToExplore = intent.getBooleanExtra("navigateToExplore", false)
        if (navigateToExplore) {
            binding.bnvMain.selectedItemId = R.id.menu_explore
            replaceFragment<ExploreFragment>()
        }
    }

    private fun replaceFragment(item: MenuItem): Boolean {
        when (FragmentType.valueOf(item.itemId)) {
            HOME -> replaceFragment<HomeFragment>()
            EXPLORE -> replaceFragment<ExploreFragment>()
            FEED -> replaceFragment<FeedFragment>()
            MY_PAGE -> {
                if (mainViewModel.mainUiState.value?.isLogin == true) {
                    replaceFragment<MyPageFragment>()
                } else {
                    showLoginRequestDialog()
                }
            }
        }
        return true
    }

    private inline fun <reified T : Fragment> replaceFragment() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_main)
            setReorderingAllowed(true)
        }
    }

    private enum class FragmentType(@IntegerRes private val resId: Int) {
        HOME(R.id.menu_home),
        EXPLORE(R.id.menu_explore),
        FEED(R.id.menu_feed),
        MY_PAGE(R.id.menu_my_page)
        ;

        companion object {
            fun valueOf(id: Int): FragmentType = values().find { fragmentView ->
                fragmentView.resId == id
            } ?: throw IllegalArgumentException()
        }
    }

    private fun showLoginRequestDialog() {
        val dialog = LoginRequestDialogFragment.newInstance()
        dialog.show(supportFragmentManager, LoginRequestDialogFragment.TAG)
    }

    private fun handleExploreNavigation() {
        val navigateToExplore = intent.getBooleanExtra("navigateToExplore", false)
        if (navigateToExplore) {
            selectExploreFragment()
        }
    }

    private fun selectExploreFragment() {
        binding.bnvMain.selectedItemId = R.id.menu_explore
        replaceFragment<ExploreFragment>()
    }

    companion object {
        private const val EXTRA_IS_LOGIN = "isLogin"

        fun getIntent(context: Context, isLogin: Boolean): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(EXTRA_IS_LOGIN, isLogin)
            return intent
        }
    }
}
