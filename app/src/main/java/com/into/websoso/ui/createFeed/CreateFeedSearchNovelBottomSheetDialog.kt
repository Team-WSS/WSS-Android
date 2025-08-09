package com.into.websoso.ui.createFeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.into.websoso.R.layout.dialog_create_feed_search_novel
import com.into.websoso.core.common.ui.base.BaseBottomSheetDialog
import com.into.websoso.core.common.util.InfiniteScrollListener
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.string.novel_inquire_link
import com.into.websoso.databinding.DialogCreateFeedSearchNovelBinding
import com.into.websoso.ui.createFeed.adapter.SearchNovelAdapter
import com.into.websoso.ui.createFeed.adapter.SearchNovelItemType.Loading
import com.into.websoso.ui.createFeed.adapter.SearchNovelItemType.Novels
import com.into.websoso.ui.createFeed.model.SearchNovelUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateFeedSearchNovelBottomSheetDialog :
    BaseBottomSheetDialog<DialogCreateFeedSearchNovelBinding>(dialog_create_feed_search_novel) {
    @Inject
    lateinit var tracker: Tracker

    private val createFeedViewModel: CreateFeedViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val searchNovelAdapter: SearchNovelAdapter by lazy { SearchNovelAdapter(::onNovelClick) }

    private fun onNovelClick(novelId: Long) {
        singleEventHandler.throttleFirst(300) {
            createFeedViewModel.updateSelectedNovel(novelId)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupAdapter()
        setupObserver()
        setupNavigateToInquireNovel()
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
                ),
            )
        }
    }

    private fun onSearchNovelClick() {
        binding.ivCreateFeedSearchNovelClose.setOnClickListener { dismiss() }
        binding.wsetCreateFeedSearchNovel.setOnWebsosoSearchActionListener { _, _, _ ->
            singleEventHandler.throttleFirst {
                createFeedViewModel.updateSearchedNovels(binding.wsetCreateFeedSearchNovel.getWebsosoSearchText())
            }
            true
        }
        binding.tvCreateFeedSearchNovelConnectButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                createFeedViewModel.updateSelectedNovel()
                dismiss()
            }
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
        val hasSelected = uiState.novels.any { it.isSelected }

        when (hasSelected) {
            true -> binding.tvCreateFeedSearchNovelConnectButton.visibility = View.VISIBLE
            false -> binding.tvCreateFeedSearchNovelConnectButton.visibility = View.INVISIBLE
        }

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
        val novels = uiState.novels.map { Novels(it) }
        when (uiState.isLoadable) {
            true -> searchNovelAdapter.submitList(novels + Loading)
            false -> searchNovelAdapter.submitList(novels)
        }
    }

    private fun setupNavigateToInquireNovel() {
        tracker.trackEvent("contact_novel_connect")
        val inquireUrl = getString(novel_inquire_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
        binding.tvCreateFeedAddNovelInquireButton.setOnClickListener {
            startActivity(intent)
        }
    }

    companion object {
        const val CREATE_FEED_SEARCH_NOVEL_TAG = "CREATE_FEED_SEARCH_NOVEL_TAG"

        fun newInstance(): CreateFeedSearchNovelBottomSheetDialog = CreateFeedSearchNovelBottomSheetDialog()
    }
}
