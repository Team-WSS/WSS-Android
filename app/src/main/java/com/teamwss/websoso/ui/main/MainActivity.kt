package com.teamwss.websoso.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityMainBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.feed.FeedFragment
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.EXPLORE
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.FEED
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.HOME
import com.teamwss.websoso.ui.main.MainActivity.FragmentType.MY_PAGE
import com.teamwss.websoso.ui.main.explore.ExploreFragment
import com.teamwss.websoso.ui.main.home.HomeFragment
import com.teamwss.websoso.ui.myPage.MyPageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setBottomNavigationView()
    }

    private fun setBottomNavigationView() {
        binding.bnvMain.selectedItemId = R.id.menu_home
        replaceFragment<HomeFragment>()

        binding.bnvMain.setOnItemSelectedListener(::replaceFragment)
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

    companion object {

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }
    }
}
