package com.teamwss.websoso.ui.explore.normalExplore

import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNormalExploreBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class NormalExploreActivity :
    BindingActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_explore)

        setupUI()
    }

    private fun setupUI() {
        setTranslucentOnStatusBar()
        setFocusSearchBar()
        setRecyclerViewAdapter()
    }

    private fun setTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun setFocusSearchBar() {
        binding.etNormalExploreSearchContent.requestFocus()
    }

    private fun setRecyclerViewAdapter() = Unit
}