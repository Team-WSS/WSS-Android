package com.into.websoso.ui.normalExplore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.into.websoso.R
import com.into.websoso.R.string.novel_inquire_link
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom.NormalExploreBack
import com.into.websoso.core.common.util.InfiniteScrollListener
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.databinding.ActivityNormalExploreBinding
import com.into.websoso.ui.normalExplore.adapter.NormalExploreAdapter
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Header
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Loading
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Novels
import com.into.websoso.ui.normalExplore.model.NormalExploreUiState
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NormalExploreActivity : BaseActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {
    @Inject
    lateinit var tracker: Tracker

    private val normalExploreAdapter: NormalExploreAdapter by lazy {
        NormalExploreAdapter(
            ::navigateToNovelDetail,
            ::navigateToInquire,
        )
    }
    private val normalExploreViewModel: NormalExploreViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupUI()
        onSearchTextEditorActionListener()
        setupObserver()
        handleBackPressed()
    }

    private fun bindViewModel() {
        binding.normalExploreViewModel = normalExploreViewModel
        binding.lifecycleOwner = this
    }

    private fun setupUI() {
        binding.apply {
            etNormalExploreSearchContent.requestFocus()
            rvNormalExploreResult.apply {
                adapter = normalExploreAdapter
                addOnScrollListener(
                    InfiniteScrollListener.of(
                        singleEventHandler = singleEventHandler,
                        event = { normalExploreViewModel?.updateSearchResult(false) },
                    ),
                )
            }
            onClick = onNormalExploreButtonClick()
        }
    }

    private fun onSearchTextEditorActionListener() {
        binding.apply {
            etNormalExploreSearchContent.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    normalExploreViewModel?.updateSearchResult(isSearchButtonClick = true)
                        ?: throw IllegalStateException()
                    binding.etNormalExploreSearchContent.clearFocus()
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding.etNormalExploreSearchContent.windowToken,
            0,
        )
    }

    private fun onNormalExploreButtonClick() =
        object : NormalExploreClickListener {
            override fun onBackButtonClick() {
                setResult(NormalExploreBack.RESULT_OK)
                finish()
            }

            override fun onSearchButtonClick() {
                singleEventHandler.throttleFirst {
                    tracker.trackEvent("click_search_result")
                    normalExploreViewModel.updateSearchResult(isSearchButtonClick = true)
                    binding.etNormalExploreSearchContent.clearFocus()
                    hideKeyboard()
                }
            }

            override fun onSearchCancelButtonClick() {
                singleEventHandler.throttleFirst {
                    normalExploreViewModel.updateSearchWordEmpty()
                    showKeyboard()
                }
            }

            override fun onNovelInquireButtonClick() {
                tracker.trackEvent("contact_novel_search")
                val inquireUrl = getString(novel_inquire_link)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
                startActivity(intent)
            }
        }

    private fun showKeyboard() {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.etNormalExploreSearchContent.requestFocus()
        inputMethodManager.showSoftInput(
            binding.etNormalExploreSearchContent,
            InputMethodManager.SHOW_IMPLICIT,
        )
    }

    private fun navigateToNovelDetail(novelId: Long) {
        singleEventHandler.throttleFirst {
            val intent = NovelDetailActivity.getIntent(this, novelId)
            startActivity(intent)
        }
    }

    private fun navigateToInquire() {
        val inquireUrl = getString(novel_inquire_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
        startActivity(intent)
    }

    private fun setupObserver() {
        normalExploreViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> {
                    binding.wllNormalExplore.setWebsosoLoadingVisibility(true)
                    binding.wllNormalExplore.setErrorLayoutVisibility(false)
                }

                uiState.error -> {
                    binding.wllNormalExplore.setWebsosoLoadingVisibility(false)
                    binding.wllNormalExplore.setErrorLayoutVisibility(true)
                }

                else -> {
                    binding.wllNormalExplore.setWebsosoLoadingVisibility(false)
                    binding.wllNormalExplore.setErrorLayoutVisibility(false)
                    updateView(uiState)
                }
            }
        }

        normalExploreViewModel.searchWord.observe(this) {
            normalExploreViewModel.validateSearchWordClearButton()
        }
    }

    private fun updateView(uiState: NormalExploreUiState) {
        val header = Header(uiState.novelCount)
        val novels = uiState.novels.map { Novels(it) }

        if (uiState.novels.isNotEmpty()) {
            when (uiState.isLoadable) {
                true -> normalExploreAdapter.submitList(listOf(header) + novels + Loading)
                false -> normalExploreAdapter.submitList(listOf(header) + novels)
            }
        }
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            setResult(NormalExploreBack.RESULT_OK)
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, NormalExploreActivity::class.java)
    }
}
