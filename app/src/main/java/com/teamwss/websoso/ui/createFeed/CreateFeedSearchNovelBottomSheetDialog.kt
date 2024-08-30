package com.teamwss.websoso.ui.createFeed

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R.layout.dialog_create_feed_search_novel
import com.teamwss.websoso.common.ui.base.BaseBottomSheetDialog
import com.teamwss.websoso.common.util.InfiniteScrollListener
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.DialogCreateFeedSearchNovelBinding
import com.teamwss.websoso.ui.createFeed.adapter.SearchNovelAdapter
import com.teamwss.websoso.ui.createFeed.adapter.SearchNovelItemType.Loading
import com.teamwss.websoso.ui.createFeed.adapter.SearchNovelItemType.Novels

class CreateFeedSearchNovelBottomSheetDialog :
    BaseBottomSheetDialog<DialogCreateFeedSearchNovelBinding>(dialog_create_feed_search_novel) {
    private val createFeedViewModel: CreateFeedViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val searchNovelAdapter: SearchNovelAdapter by lazy {
        SearchNovelAdapter {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupAdapter()
        setupObserver()
        binding.viewModel = createFeedViewModel
        onSearchNovelClick()
    }

    private fun setupDialog() {
        (dialog as BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            behavior.isHideable = false
            setCancelable(false)
        }
    }

    private fun setupAdapter() {
        binding.rvCreateFeedSearchNovelResult.apply {
            adapter = searchNovelAdapter
            itemAnimator = null
            setHasFixedSize(true)
            addOnScrollListener(
                InfiniteScrollListener.of(
                    singleEventHandler = singleEventHandler,
                    event = { createFeedViewModel.updateSearchedNovels() },
                )
            )
        }
    }

    private fun onSearchNovelClick() {
        binding.ivCreateFeedSearchNovelClose.setOnClickListener { dismiss() }
        binding.wsetCreateFeedSearchNovel.setOnWebsosoSearchActionListener { _, _, _ ->
            createFeedViewModel.updateSearchedNovels(binding.wsetCreateFeedSearchNovel.getWebsosoSearchText())
            true
        }
    }

    private fun setupObserver() {
        createFeedViewModel.searchNovelUiState.observe(viewLifecycleOwner) { uiState ->
            when {
                !uiState.loading -> updateView(uiState)
            }
        }
    }

    private fun updateView(uiState: SearchNovelUiState) {
        Log.d("123123", uiState.novels.count().toString())
        when (uiState.novels.isEmpty()) {
            true -> {
                binding.rvCreateFeedSearchNovelResult.visibility = View.INVISIBLE
                binding.clCreateFeedResultNotExist.visibility = View.VISIBLE
            }

            false -> {
                binding.rvCreateFeedSearchNovelResult.visibility = View.VISIBLE
                binding.clCreateFeedResultNotExist.visibility = View.INVISIBLE
            }
        }
        updateNovels(uiState)
    }

    private fun updateNovels(uiState: SearchNovelUiState) {
        //binding.clFeedNone.isVisible = feedUiState.feeds.isEmpty()
        val novels = uiState.novels.map { Novels(it) }
        when (uiState.isLoadable) {
            true -> searchNovelAdapter.submitList(novels + Loading)
            false -> searchNovelAdapter.submitList(novels)
        }
    }

    companion object {
        const val CREATE_FEED_SEARCH_NOVEL_TAG = "CREATE_FEED_SEARCH_NOVEL_TAG"

        fun newInstance(): CreateFeedSearchNovelBottomSheetDialog {
            return CreateFeedSearchNovelBottomSheetDialog()
        }
    }
}
