package com.into.websoso.ui.main.explore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseFragment
import com.into.websoso.common.util.SingleEventHandler
import com.into.websoso.data.tracker.Tracker
import com.into.websoso.databinding.FragmentExploreBinding
import com.into.websoso.ui.detailExplore.DetailExploreDialogBottomSheet
import com.into.websoso.ui.main.explore.adapter.SosoPickAdapter
import com.into.websoso.ui.normalExplore.NormalExploreActivity
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : BaseFragment<FragmentExploreBinding>(R.layout.fragment_explore) {
    @Inject
    lateinit var tracker: Tracker

    private val sosoPickAdapter: SosoPickAdapter by lazy { SosoPickAdapter(::navigateToNovelDetail) }
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val exploreViewModel: ExploreViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSosoPickAdapter()
        onNormalSearchButtonClick()
        onDetailExploreButtonClick()
        onReloadPageButtonClick()
        setupObserver()
        tracker.trackEvent("search")
    }

    private fun initSosoPickAdapter() {
        binding.rvExploreSosoPick.adapter = sosoPickAdapter
        binding.rvExploreSosoPick.setHasFixedSize(true)
    }

    private fun navigateToNovelDetail(novelId: Long) {
        tracker.trackEvent(
            eventName = "soso_pick",
            properties = mapOf("novelId" to novelId)
        )
        val intent = NovelDetailActivity.getIntent(requireContext(), novelId)
        startActivity(intent)
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
                showDetailExploreDialog()
            }
        }
    }

    private fun showDetailExploreDialog() {
        val detailExploreBottomSheet = DetailExploreDialogBottomSheet.newInstance()
        detailExploreBottomSheet.show(childFragmentManager, DETAIL_BOTTOM_SHEET_TAG)
    }

    private fun onReloadPageButtonClick() {
        binding.wllExplore.setReloadButtonClickListener {
            binding.wllExplore.setErrorLayoutVisibility(false)
            exploreViewModel.updateSosoPicks()
        }
    }

    private fun setupObserver() {
        exploreViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> binding.wllExplore.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllExplore.setLoadingLayoutVisibility(false)
                !uiState.loading -> {
                    binding.wllExplore.setWebsosoLoadingVisibility(false)
                    sosoPickAdapter.submitList(uiState.sosoPicks)
                }
            }
        }
    }

    companion object {
        private const val DETAIL_BOTTOM_SHEET_TAG = "DetailExploreDialogBottomSheet"
    }
}