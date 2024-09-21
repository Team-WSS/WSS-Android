package com.teamwss.websoso.ui.normalExplore

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.util.InfiniteScrollListener
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.ActivityNormalExploreBinding
import com.teamwss.websoso.ui.main.home.HomeFragment
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreAdapter
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.Header
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.Loading
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.Novels
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreUiState
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NormalExploreActivity :
    BaseActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {
    private val normalExploreAdapter: NormalExploreAdapter by lazy { NormalExploreAdapter(::navigateToNovelDetail) }
    private val normalExploreViewModel: NormalExploreViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val fromViewName by lazy { intent.getStringExtra(FROM_VIEW_NAME) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupUI()
        onSearchTextEditorActionListener()
        setupObserver()
        onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }
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
                    InfiniteScrollListener.of(singleEventHandler = singleEventHandler,
                        event = { normalExploreViewModel?.updateSearchResult(false) })
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

    private fun onNormalExploreButtonClick() = object : NormalExploreClickListener {

        override fun onBackButtonClick() {
            if (fromViewName == HomeFragment.SOURCE_HOME) {
                setResult(Activity.RESULT_OK)
            }
            finish()
        }

        override fun onSearchButtonClick() {
            normalExploreViewModel.updateSearchResult(isSearchButtonClick = true)
            binding.etNormalExploreSearchContent.clearFocus()
            hideKeyboard()
        }

        override fun onSearchCancelButtonClick() {
            normalExploreViewModel.updateSearchWordEmpty()
            showKeyboard()
        }

        override fun onNovelInquireButtonClick() {
            val inquireUrl = getString(R.string.inquire_link)
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
        val intent = NovelDetailActivity.getIntent(this, novelId)
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
        if (fromViewName == HomeFragment.SOURCE_HOME) {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }

    companion object {
        private const val FROM_VIEW_NAME = "FROM_VIEW_NAME"

        fun getIntent(context: Context): Intent = Intent(context, NormalExploreActivity::class.java)

        fun getIntent(context: Context, fromViewName: String): Intent =
            Intent(context, NormalExploreActivity::class.java).apply {
                putExtra(FROM_VIEW_NAME, fromViewName)
            }
    }
}