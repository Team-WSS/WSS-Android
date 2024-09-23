package com.teamwss.websoso.ui.storage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityStorageBinding
import com.teamwss.websoso.ui.main.MainActivity
import com.teamwss.websoso.ui.storage.adapter.StorageViewPagerAdapter
import com.teamwss.websoso.ui.storage.model.StorageTab
import com.teamwss.websoso.ui.storage.model.StorageUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StorageActivity : BaseActivity<ActivityStorageBinding>(R.layout.activity_storage) {
    private val storageViewModel: StorageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel()
        setupViewPagerAndTabLayout()
        onSortButtonClick()
        onBackButtonClick()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.storageViewModel = storageViewModel
    }

    private fun setupViewPagerAndTabLayout() {
        setupViewPager()
        setupTabLayoutWithViewPager()
        setupObserver()
    }

    private fun setupViewPager() {
        val pagerAdapter = StorageViewPagerAdapter(
            novels = emptyList(),
            navigateToExplore = ::navigateToExploreFragment,
        )
        binding.vpStorage.adapter = pagerAdapter
    }

    private fun setupTabLayoutWithViewPager() {
        binding.vpStorage.adapter?.let {
            TabLayoutMediator(binding.tlStorage, binding.vpStorage) { tab, position ->
                tab.text = StorageTab.fromPosition(position).title
            }.attach()

            binding.tlStorage.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        onSortTabSelected(it.position)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun onSortTabSelected(position: Int) {
        val selectedTab = StorageTab.fromPosition(position)
        storageViewModel.updateReadStatus(
            selectedTab.readStatus,
            forceLoad = true,
        )
    }

    private fun setupObserver() {
        storageViewModel.uiState.observe(this) { uiState ->
            updateViewPagerAdapter(uiState)
        }
    }

    private fun updateViewPagerAdapter(uiState: StorageUiState) {
        val adapter = binding.vpStorage.adapter as? StorageViewPagerAdapter
        adapter?.updateNovels(uiState.storageNovels)
    }

    private fun navigateToExploreFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigateToExplore", true)
        startActivity(intent)
    }

    private fun onSortButtonClick() {
        val clickHandler = SortClickHandler(storageViewModel)
        binding.onClick = clickHandler
    }

    private fun onBackButtonClick() {
        binding.ivStorageDetailBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, StorageActivity::class.java)
        }
    }
}
