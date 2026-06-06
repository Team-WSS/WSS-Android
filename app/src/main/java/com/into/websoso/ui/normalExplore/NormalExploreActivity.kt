package com.into.websoso.ui.normalExplore

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.into.websoso.R.color.gray_300_52515F
import com.into.websoso.R.color.gray_50_F4F5F8
import com.into.websoso.R.layout.activity_normal_explore
import com.into.websoso.R.style.body3
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel.KeywordModel
import com.into.websoso.core.common.ui.model.ResultFrom.NormalExploreBack
import com.into.websoso.core.common.util.InfiniteScrollListener
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.string.novel_inquire_link
import com.into.websoso.databinding.ActivityNormalExploreBinding
import com.into.websoso.ui.detailExplore.info.model.Genre
import com.into.websoso.ui.detailExploreResult.DetailExploreResultActivity
import com.into.websoso.ui.detailExploreResult.model.DetailExploreFilteredModel
import com.into.websoso.ui.main.explore.adapter.SosoPickAdapter
import com.into.websoso.ui.normalExplore.adapter.GenreSearchAdapter
import com.into.websoso.ui.normalExplore.adapter.NormalExploreAdapter
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Header
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Loading
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Novels
import com.into.websoso.ui.normalExplore.adapter.RecentSearchAdapter
import com.into.websoso.ui.normalExplore.model.GenreSearchModel
import com.into.websoso.ui.normalExplore.model.NormalExploreUiState
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NormalExploreActivity : BaseActivity<ActivityNormalExploreBinding>(activity_normal_explore) {
    @Inject
    lateinit var tracker: Tracker

    private val normalExploreAdapter: NormalExploreAdapter by lazy {
        NormalExploreAdapter(
            ::navigateToNovelDetail,
            ::navigateToInquire,
        )
    }
    private val recentSearchAdapter: RecentSearchAdapter by lazy {
        RecentSearchAdapter(
            ::searchRecentSearch,
            normalExploreViewModel::deleteRecentSearch,
        )
    }
    private val genreSearchAdapter: GenreSearchAdapter by lazy {
        GenreSearchAdapter(::navigateToDetailExploreResult)
    }
    private val sosoPickAdapter: SosoPickAdapter by lazy { SosoPickAdapter(::navigateToNovelDetailFromSosoPick) }
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
            rvNormalExploreSosoPick.adapter = sosoPickAdapter
            rvNormalExploreRecentSearch.adapter = recentSearchAdapter
            rvNormalExploreGenreSearch.adapter = genreSearchAdapter
            genreSearchAdapter.submitList(GenreSearchModel.items)
            onClick = onNormalExploreButtonClick()
        }
    }

    private fun onSearchTextEditorActionListener() {
        binding.apply {
            etNormalExploreSearchContent.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == IME_ACTION_SEARCH) {
                    searchKeyword(
                        binding.etNormalExploreSearchContent.text
                            ?.toString()
                            .orEmpty(),
                    )
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
                    searchKeyword(
                        binding.etNormalExploreSearchContent.text
                            ?.toString()
                            .orEmpty(),
                    )
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
                val intent = Intent(ACTION_VIEW, Uri.parse(inquireUrl))
                startActivity(intent)
            }
        }

    private fun searchRecentSearch(keyword: String) {
        singleEventHandler.throttleFirst {
            searchKeyword(keyword)
        }
    }

    private fun searchKeyword(keyword: String) {
        normalExploreViewModel.updateSearchWord(keyword)
        normalExploreViewModel.updateSearchResult(isSearchButtonClick = true)
        binding.etNormalExploreSearchContent.clearFocus()
        hideKeyboard()
    }

    private fun showKeyboard() {
        val inputMethodManager =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        binding.etNormalExploreSearchContent.requestFocus()
        inputMethodManager.showSoftInput(
            binding.etNormalExploreSearchContent,
            InputMethodManager.SHOW_IMPLICIT,
        )
    }

    private fun navigateToDetailExploreResult(genre: Genre) {
        singleEventHandler.throttleFirst {
            val intent = DetailExploreResultActivity.getIntent(
                context = this,
                detailExploreFilteredModel = DetailExploreFilteredModel(
                    genres = listOf(genre),
                ),
            )
            startActivity(intent)
        }
    }

    private fun navigateToDetailExploreResult(keyword: KeywordModel) {
        singleEventHandler.throttleFirst {
            val intent = DetailExploreResultActivity.getIntent(
                context = this,
                detailExploreFilteredModel = DetailExploreFilteredModel(
                    keywordIds = listOf(keyword.keywordId),
                ),
            )
            startActivity(intent)
        }
    }

    private fun navigateToNovelDetail(novelId: Long) {
        singleEventHandler.throttleFirst {
            val intent = NovelDetailActivity.getIntent(this, novelId)
            startActivity(intent)
        }
    }

    private fun navigateToNovelDetailFromSosoPick(novelId: Long) {
        singleEventHandler.throttleFirst {
            tracker.trackEvent("soso_pick", mapOf("novelId" to novelId))
            val intent = NovelDetailActivity.getIntent(this, novelId)
            startActivity(intent)
        }
    }

    private fun navigateToInquire() {
        val inquireUrl = getString(novel_inquire_link)
        val intent = Intent(ACTION_VIEW, Uri.parse(inquireUrl))
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

        normalExploreViewModel.sosoPicks.observe(this) { sosoPicks ->
            sosoPickAdapter.submitList(sosoPicks)
        }

        normalExploreViewModel.recentSearches.observe(this) { recentSearches ->
            recentSearchAdapter.submitList(recentSearches)
        }

        normalExploreViewModel.keywordSearches.observe(this) { keywordSearches ->
            updateKeywordSearchChips(keywordSearches)
        }
    }

    private fun updateKeywordSearchChips(keywordSearches: List<KeywordModel>) {
        val keywordChipGroup = binding.wcgNormalExploreKeywordSearch
        keywordChipGroup.removeAllViews()
        keywordSearches.forEach { keyword ->
            WebsosoChip(this@NormalExploreActivity)
                .apply {
                    setWebsosoChipText(keyword.keywordName)
                    setWebsosoChipTextAppearance(body3)
                    setWebsosoChipTextColor(gray_300_52515F)
                    setWebsosoChipBackgroundColor(gray_50_F4F5F8)
                    setWebsosoChipPaddingVertical(KEYWORD_CHIP_VERTICAL_PADDING.toFloatPxFromDp())
                    setWebsosoChipPaddingHorizontal(KEYWORD_CHIP_HORIZONTAL_PADDING.toFloatPxFromDp())
                    setWebsosoChipRadius(KEYWORD_CHIP_RADIUS.toFloatPxFromDp())
                    setOnWebsosoChipClick { navigateToDetailExploreResult(keyword) }
                }.also { websosoChip -> keywordChipGroup.addChip(websosoChip) }
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
        } else {
            normalExploreAdapter.submitList(emptyList())
        }
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            setResult(NormalExploreBack.RESULT_OK)
            finish()
        }
    }

    companion object {
        const val SEARCH_AUTHOR = "SEARCH_AUTHOR"
        private const val KEYWORD_CHIP_RADIUS = 20f
        private const val KEYWORD_CHIP_VERTICAL_PADDING = 7f
        private const val KEYWORD_CHIP_HORIZONTAL_PADDING = 13f

        fun getIntent(
            context: Context,
            searchAuthor: String = "",
        ): Intent =
            Intent(context, NormalExploreActivity::class.java).apply {
                putExtra(SEARCH_AUTHOR, searchAuthor)
            }
    }
}
