package com.into.websoso.ui.main.myPage

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.chip.Chip
import com.into.websoso.R.color.gray_300_52515F
import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.color.primary_50_F1EFFF
import com.into.websoso.R.color.transparent
import com.into.websoso.R.color.white
import com.into.websoso.R.layout.fragment_my_page
import com.into.websoso.R.style.body2
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.ui.model.ResultFrom.ProfileEditSuccess
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.setListViewHeightBasedOnChildren
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.drawable.img_loading_thumbnail
import com.into.websoso.core.resource.R.string.my_library_attractive_point_fixed_text
import com.into.websoso.data.model.GenrePreferenceEntity
import com.into.websoso.data.model.NovelPreferenceEntity
import com.into.websoso.databinding.FragmentMyPageBinding
import com.into.websoso.ui.main.MainViewModel
import com.into.websoso.ui.main.myPage.myLibrary.MyLibraryViewModel
import com.into.websoso.ui.main.myPage.myLibrary.adapter.RestGenrePreferenceAdapter
import com.into.websoso.ui.main.myPage.myLibrary.model.MyLibraryUiState
import com.into.websoso.ui.profileEdit.ProfileEditActivity
import com.into.websoso.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(fragment_my_page) {
    @Inject
    lateinit var tracker: Tracker

    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val myLibraryViewModel: MyLibraryViewModel by viewModels()

    private val restGenrePreferenceAdapter: RestGenrePreferenceAdapter by lazy { RestGenrePreferenceAdapter() }

    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        when (result.resultCode) {
            ProfileEditSuccess.RESULT_OK -> {
                myPageViewModel.updateUserProfile()
                mainViewModel.updateUserInfo()
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupRestGenrePreferenceAdapter()
        setupItemVisibilityOnToolBar()
        onSettingButtonClick()
        setupObserver()
        onProfileEditClick()
        onStorageButtonClick()
        tracker.trackEvent("mypage")
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.updateUserInfo()
        myPageViewModel.updateUserProfile()
    }

    private fun bindViewModel() {
        binding.myPageViewModel = myPageViewModel
        binding.myLibraryViewModel = myLibraryViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupRestGenrePreferenceAdapter() {
        binding.lvMyLibraryRestGenre.adapter = restGenrePreferenceAdapter
    }

    private fun setupItemVisibilityOnToolBar() {
        binding.ablMyPage.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = (totalScrollRange.toFloat() + verticalOffset) / totalScrollRange
            updateToolbarUi(percentage <= TOOLBAR_COLLAPSE_THRESHOLD)
        }
    }

    private fun updateToolbarUi(isCollapsed: Boolean) {
        with(binding) {
            val color = if (isCollapsed) white else transparent
            tbMyPage.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    color,
                ),
            )

            tvMyPageStickyTitle.isVisible = isCollapsed
            clMyPageUserProfile.isVisible = !isCollapsed
        }
    }

    private fun onSettingButtonClick() {
        binding.ivMyPageStickyGoToSetting.setOnClickListener {
            val intent = SettingActivity.getIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun setupObserver() {
        myPageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> {
                    binding.wllMyPage.setWebsosoLoadingVisibility(true)
                }

                uiState.error -> {
                    binding.wllMyPage.setErrorLayoutVisibility(true)
                }

                !uiState.loading -> {
                    binding.wllMyPage.setErrorLayoutVisibility(false)
                    binding.wllMyPage.setWebsosoLoadingVisibility(false)
                    setUpMyProfileImage(uiState.myProfile?.avatarImage.orEmpty())
                }
            }
        }

        myLibraryViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            handleLibraryLoadingState(uiState)

            if (!uiState.isLoading && !uiState.isError) {
                val hasNoGenre = myLibraryViewModel.hasNoPreferences()
                val hasNoNovel = !myLibraryViewModel.hasNovelPreferences()

                if (hasNoGenre) {
                    binding.clMyLibraryGenrePreference.isVisible = false
                    binding.clMyLibraryUnknownPreference.isVisible = true

                    binding.viewMyPageNovelPreferenceDivider.isVisible = false
                    binding.clMyLibraryNovelPreference.isVisible = false
                    binding.clMyLibraryUnknownNovelPreference.isVisible = false
                } else {
                    binding.clMyLibraryGenrePreference.isVisible = true
                    binding.clMyLibraryUnknownPreference.isVisible = false

                    binding.viewMyPageNovelPreferenceDivider.isVisible = true

                    if (hasNoNovel) {
                        binding.clMyLibraryNovelPreference.isVisible = false
                        binding.clMyLibraryUnknownNovelPreference.isVisible = true
                    } else {
                        binding.clMyLibraryNovelPreference.isVisible = true
                        binding.clMyLibraryUnknownNovelPreference.isVisible = false
                    }
                }

                binding.clMyLibraryAttractivePoints.isVisible =
                    myLibraryViewModel.hasAttractivePoints()

                restGenrePreferenceAdapter.updateRestGenrePreferenceData(uiState.restGenres)
                updateRestGenrePreferenceVisibility(uiState.isGenreListVisible)
                uiState.novelPreferences?.let { updateNovelPreferencesKeywords(it) }
                updateDominantGenres(uiState.topGenres)
                updateGenreBadgeTitle(uiState.totalBadgeCount)

                applyTextColors(
                    uiState.translatedAttractivePoints.joinToString(", ") +
                        getString(my_library_attractive_point_fixed_text),
                )
            }
        }
    }

    private fun setUpMyProfileImage(myProfileUrl: String) {
        val updatedMyProfileImageUrl = binding.root.getS3ImageUrl(myProfileUrl)
        binding.ivMyPageUserProfile.load(updatedMyProfileImageUrl) {
            crossfade(true)
            error(img_loading_thumbnail)
            transformations(CircleCropTransformation())
        }
    }

    private fun handleLibraryLoadingState(uiState: MyLibraryUiState) {
        when {
            uiState.isLoading -> {
                binding.wllMyPage.setWebsosoLoadingVisibility(true)
            }

            uiState.isError -> {
                binding.wllMyPage.setWebsosoLoadingVisibility(false)
                binding.wllMyPage.setErrorLayoutVisibility(true)
            }

            else -> {
                binding.wllMyPage.setWebsosoLoadingVisibility(false)
                binding.wllMyPage.setErrorLayoutVisibility(false)
            }
        }
    }

    private fun updateRestGenrePreferenceVisibility(isVisible: Boolean) {
        binding.lvMyLibraryRestGenre.isVisible = isVisible
        if (isVisible) {
            binding.lvMyLibraryRestGenre.setListViewHeightBasedOnChildren()
        }
    }

    private fun applyTextColors(combinedText: String) {
        val primary100 = requireContext().getColor(primary_100_6A5DFD)
        val gray300 = requireContext().getColor(gray_300_52515F)

        val spannableStringBuilder = SpannableStringBuilder()

        val fixedText = getString(my_library_attractive_point_fixed_text)

        val splitText = combinedText.split(fixedText)

        if (splitText.isNotEmpty()) {
            val attractivePoints =
                SpannableString(splitText[0]).apply {
                    setSpan(
                        ForegroundColorSpan(primary100),
                        0,
                        length,
                        SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
            spannableStringBuilder.append(attractivePoints)

            val fixedSpannable =
                SpannableString(fixedText).apply {
                    setSpan(
                        ForegroundColorSpan(gray300),
                        0,
                        length,
                        SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
            spannableStringBuilder.append(fixedSpannable)
        } else {
            val spannable = SpannableString(combinedText).apply {
                setSpan(
                    ForegroundColorSpan(primary100),
                    0,
                    length,
                    SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
            spannableStringBuilder.append(spannable)
        }

        binding.tvMyLibraryAttractivePoints.text = spannableStringBuilder
    }

    private fun updateNovelPreferencesKeywords(novelPreferences: NovelPreferenceEntity) {
        val existingKeywords = mutableSetOf<String>()

        for (i in 0 until binding.wcgMyLibraryAttractivePoints.childCount) {
            val chip = binding.wcgMyLibraryAttractivePoints.getChildAt(i) as? Chip
            chip?.text?.let { existingKeywords.add(it.toString()) }
        }

        novelPreferences.keywords.forEach { keyword ->
            val keywordText = "${keyword.keywordName} ${keyword.keywordCount}"
            if (!existingKeywords.contains(keywordText)) {
                binding.wcgMyLibraryAttractivePoints.addView(createKeywordChip(keyword))
                existingKeywords.add(keywordText)
            }
        }
    }

    private fun createKeywordChip(data: NovelPreferenceEntity.KeywordEntity): Chip =
        WebsosoChip(requireContext()).apply {
            text = "${data.keywordName} ${data.keywordCount}"
            isCheckable = false
            isChecked = false
            isEnabled = false

            setChipBackgroundColorResource(primary_50_F1EFFF)
            setTextColor(ContextCompat.getColor(requireContext(), primary_100_6A5DFD))
            setTextAppearance(body2)
        }

    private fun updateDominantGenres(topGenres: List<GenrePreferenceEntity>) {
        topGenres.forEachIndexed { index, genrePreferenceEntity ->
            val updatedGenreImageUrl = binding.root.getS3ImageUrl(genrePreferenceEntity.genreImage)

            when (index) {
                0 -> binding.ivMyLibraryDominantGenreFirstLogo.load(updatedGenreImageUrl)
                1 -> binding.ivMyLibraryDominantGenreSecondLogo.load(updatedGenreImageUrl)
                2 -> binding.ivMyLibraryDominantGenreThirdLogo.load(updatedGenreImageUrl)
            }
        }
    }

    private fun updateGenreBadgeTitle(count: Int) {
        val primary100 = ContextCompat.getColor(
            requireContext(),
            primary_100_6A5DFD,
        )
        val countText = count.toString()
        val fullText = getString(com.into.websoso.core.resource.R.string.my_page_badge, count)

        val spannable = SpannableStringBuilder(fullText)
        val start = fullText.indexOf(countText)
        val end = start + countText.length

        if (start != -1) {
            spannable.setSpan(
                ForegroundColorSpan(primary100),
                start,
                end,
                SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }
        binding.tvMyLibraryGenrePreferenceTitle.text = spannable
    }

    private fun onProfileEditClick() {
        binding.ivMyPageUserProfileEdit.setOnClickListener {
            startActivityLauncher.launch(
                ProfileEditActivity.getIntent(
                    requireContext(),
                ),
            )
        }
    }

    private fun onStorageButtonClick() {
        binding.llMyLibraryStorage.setOnClickListener {
            singleEventHandler.throttleFirst {
                requireActivity().supportFragmentManager.setFragmentResult(
                    "NAVIGATE_TO_LIBRARY_FRAGMENT",
                    Bundle.EMPTY,
                )
            }
        }
    }

    companion object {
        private const val TOOLBAR_COLLAPSE_THRESHOLD = 0
        const val TAG = "MyPageFragment"
    }
}
