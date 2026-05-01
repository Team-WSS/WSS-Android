package com.into.websoso.ui.main.explore

import android.os.Bundle
import android.view.View
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.databinding.FragmentExploreBinding
import com.into.websoso.ui.detailExplore.DetailExploreActivity
import com.into.websoso.ui.normalExplore.NormalExploreActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : BaseFragment<FragmentExploreBinding>(R.layout.fragment_explore) {
    @Inject
    lateinit var tracker: Tracker

    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        onNormalSearchButtonClick()
        onDetailExploreButtonClick()
        tracker.trackEvent("search")
    }

    private fun onNormalSearchButtonClick() {
        binding.clExploreNormalSearch.setOnClickListener {
            tracker.trackEvent("general_search")
            val intent = NormalExploreActivity.getIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun onDetailExploreButtonClick() {
        binding.clExploreDetailSearch.setOnClickListener {
            singleEventHandler.throttleFirst {
                startActivity(DetailExploreActivity.getIntent(requireContext()))
            }
        }
    }

    companion object {
        const val TAG = "ExploreFragment"
    }
}
