package com.teamwss.websoso.ui.main.explore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentExploreBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.detailExplore.DetailExploreDialog
import com.teamwss.websoso.ui.main.explore.adapter.SosoPickAdapter
import com.teamwss.websoso.ui.normalExplore.NormalExploreActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : BindingFragment<FragmentExploreBinding>(R.layout.fragment_explore) {
    private val sosoPickAdapter: SosoPickAdapter by lazy { SosoPickAdapter(::navigateToNovelDetail) }
    private val exploreViewModel: ExploreViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSosoPickAdapter()
        onNormalSearchButtonClick()
        onDetailExploreButtonClick()
        setupObserveUiState()
    }

    private fun initSosoPickAdapter() {
        binding.rvExploreSosoPick.adapter = sosoPickAdapter
        binding.rvExploreSosoPick.setHasFixedSize(true)
    }

    private fun navigateToNovelDetail(novelId: Long) {
        // TODO 작품 정보 뷰로 이동
    }

    private fun onNormalSearchButtonClick() {
        binding.clExploreNormalSearch.setOnClickListener {
            val intent = NormalExploreActivity.from(requireContext())
            startActivity(intent)
        }
    }

    private fun onDetailExploreButtonClick() {
        binding.clExploreDetailSearch.setOnClickListener {
            val detailExploreBottomSheet = DetailExploreDialog()
            detailExploreBottomSheet.show(this@ExploreFragment.childFragmentManager, tag)
        }
    }

    private fun setupObserveUiState() {
        exploreViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> loading()
                uiState.error -> throw IllegalStateException()
                !uiState.loading -> sosoPickAdapter.submitList(uiState.sosoPicks)
            }
        }
    }

    private fun loading() {
        // TODO 로딩 뷰
    }
}