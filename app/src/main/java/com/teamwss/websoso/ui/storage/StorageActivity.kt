package com.teamwss.websoso.ui.storage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityStorageBinding
import com.teamwss.websoso.ui.main.MainActivity
import com.teamwss.websoso.ui.storage.adapter.StorageViewPagerAdapter
import com.teamwss.websoso.ui.storage.model.StorageTab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StorageActivity : BaseActivity<ActivityStorageBinding>(R.layout.activity_storage) {
    private val storageViewModel: StorageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewPagerAndTabLayout()
        onBackButtonClick()
    }

    private fun setupViewPagerAndTabLayout() {
        val pagerAdapter =
            StorageViewPagerAdapter(storageViewModel.novelsMap, ::navigateToExploreFragment)
        binding.vpStorage.adapter = pagerAdapter
        bindTabLayoutWithViewPager()
    }

    private fun bindTabLayoutWithViewPager() {
        TabLayoutMediator(binding.tlStorage, binding.vpStorage) { tab, position ->
            tab.text = StorageTab.fromPosition(position).title
        }.attach()
    }

    private fun onBackButtonClick() {
        binding.ivStorageDetailBackButton.setOnClickListener {
            finish()
        }
    }

    private fun navigateToExploreFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigateToExplore", true)
        startActivity(intent)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, StorageActivity::class.java)
        }
    }
}