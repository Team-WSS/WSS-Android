package com.into.websoso.ui.otherUserPage.otherUserLibrary

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.chip.Chip
import com.into.websoso.R.color.gray_300_52515F
import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.color.primary_50_F1EFFF
import com.into.websoso.R.layout.fragment_other_user_library
import com.into.websoso.R.style.body2
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.setListViewHeightBasedOnChildren
import com.into.websoso.core.resource.R.string.my_library_attractive_point_fixed_text
import com.into.websoso.data.model.GenrePreferenceEntity
import com.into.websoso.data.model.NovelPreferenceEntity
import com.into.websoso.databinding.FragmentOtherUserLibraryBinding
import com.into.websoso.ui.otherUserPage.otherUserLibrary.adapter.RestGenrePreferenceAdapter
import com.into.websoso.ui.userStorage.UserStorageActivity
import com.into.websoso.ui.userStorage.model.StorageTab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserLibraryFragment :
    BaseFragment<FragmentOtherUserLibraryBinding>(fragment_other_user_library) {
    private val otherUserLibraryViewModel: OtherUserLibraryViewModel by viewModels()
    private val restGenrePreferenceAdapter: RestGenrePreferenceAdapter by lazy {
        RestGenrePreferenceAdapter()
    }
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        updateUserId()
        setupRestGenrePreferenceAdapter()
        setupObserve()
        onStorageButtonClick()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.otherUserLibraryViewModel = otherUserLibraryViewModel
    }

    private fun setupRestGenrePreferenceAdapter() {
        binding.lvOtherUserLibraryRestGenre.adapter = restGenrePreferenceAdapter
    }

    private fun updateUserId() {
        val userId = arguments?.getLong(USER_ID_KEY) ?: 0L
        otherUserLibraryViewModel.updateUserId(userId)
    }

    private fun setupObserve() {
        otherUserLibraryViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.isLoading -> binding.wllOtherUserLibrary.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllOtherUserLibrary.setLoadingLayoutVisibility(false)
                !uiState.isLoading -> {
                    binding.wllOtherUserLibrary.setWebsosoLoadingVisibility(false)
                }
            }

            when (otherUserLibraryViewModel.hasNoPreferences()) {
                true -> {
                    binding.clOtherUserLibraryKnownPreference.visibility = View.GONE
                    binding.clOtherUserLibraryUnknownPreference.visibility = View.VISIBLE
                }

                false -> {
                    binding.clOtherUserLibraryKnownPreference.visibility = View.VISIBLE
                    binding.clOtherUserLibraryUnknownPreference.visibility = View.GONE
                }
            }

            when (otherUserLibraryViewModel.hasNovelPreferences()) {
                true -> {
                    binding.clOtherUserLibraryNovelPreference.visibility = View.VISIBLE
                    binding.clOtherUserLibraryUnknownNovelPreference.visibility = View.GONE
                }

                false -> {
                    binding.clOtherUserLibraryNovelPreference.visibility = View.GONE
                    binding.clOtherUserLibraryUnknownNovelPreference.visibility = View.VISIBLE
                }
            }

            when (otherUserLibraryViewModel.hasAttractivePoints()) {
                true -> {
                    binding.clOtherUserLibraryAttractivePoints.visibility = View.VISIBLE
                }

                false -> {
                    binding.clOtherUserLibraryAttractivePoints.visibility = View.GONE
                }
            }

            restGenrePreferenceAdapter.updateRestGenrePreferenceData(uiState.restGenres)
            updateRestGenrePreferenceVisibility(uiState.isGenreListVisible)

            uiState.novelPreferences?.let { updateNovelPreferencesKeywords(it) }
            updateDominantGenres(uiState.topGenres)

            applyTextColors(
                uiState.translatedAttractivePoints.joinToString(", ") + getString(
                    my_library_attractive_point_fixed_text,
                ),
            )
        }
    }

    private fun updateRestGenrePreferenceVisibility(isVisible: Boolean) {
        binding.lvOtherUserLibraryRestGenre.isVisible = isVisible
        if (isVisible) {
            binding.lvOtherUserLibraryRestGenre.setListViewHeightBasedOnChildren()
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

        binding.tvOtherUserLibraryAttractivePoints.text = spannableStringBuilder
    }

    private fun updateNovelPreferencesKeywords(novelPreferences: NovelPreferenceEntity) {
        novelPreferences.keywords.forEach { keyword ->
            binding.wcgOtherUserLibraryAttractivePoints.addView(createKeywordChip(keyword))
        }
    }

    private fun createKeywordChip(data: NovelPreferenceEntity.KeywordEntity): Chip =
        WebsosoChip(requireContext()).apply {
            text = "${data.keywordName} ${data.keywordCount}"
            isChecked = false

            setChipBackgroundColorResource(primary_50_F1EFFF)
            setTextColor(ContextCompat.getColor(requireContext(), primary_100_6A5DFD))
            setTextAppearance(body2)
        }

    private fun updateDominantGenres(topGenres: List<GenrePreferenceEntity>) {
        topGenres.forEachIndexed { index, genrePreferenceEntity ->
            val updatedGenreImageUrl = binding.root.getS3ImageUrl(genrePreferenceEntity.genreImage)

            when (index) {
                0 -> binding.ivOtherUserLibraryDominantGenreFirstLogo.load(updatedGenreImageUrl)
                1 -> binding.ivOtherUserLibraryDominantGenreSecondLogo.load(updatedGenreImageUrl)
                2 -> binding.ivOtherUserLibraryDominantGenreThirdLogo.load(updatedGenreImageUrl)
            }
        }
    }

    private fun onStorageButtonClick() {
        val userId = requireNotNull(otherUserLibraryViewModel.userId.value)

        val clickMappings = mapOf(
            binding.clOtherUserLibraryTopBar to StorageTab.INTEREST.readStatus,
            binding.llOtherUserLibraryStorageInteresting to StorageTab.INTEREST.readStatus,
            binding.llOtherUserLibraryStorageWatching to StorageTab.WATCHING.readStatus,
            binding.llOtherUserLibraryStorageWatched to StorageTab.WATCHED.readStatus,
            binding.llOtherUserLibraryStorageQuit to StorageTab.QUIT.readStatus,
        )

        clickMappings.forEach { (view, readStatus) ->
            view.setOnClickListener {
                singleEventHandler.throttleFirst {
                    navigateToUserStorageActivity(userId, readStatus)
                }
            }
        }
    }

    private fun navigateToUserStorageActivity(
        userId: Long,
        readStatus: String,
    ) {
        startActivity(
            UserStorageActivity.getIntent(
                context = requireContext(),
                source = UserStorageActivity.SOURCE_OTHER_USER_LIBRARY,
                userId = userId,
                readStatus = readStatus,
            ),
        )
    }

    override fun onResume() {
        super.onResume()

        view?.requestLayout()
    }

    companion object {
        private const val USER_ID_KEY = "USER_ID"

        fun newInstance(userId: Long) =
            OtherUserLibraryFragment().apply {
                arguments = Bundle().apply {
                    putLong(USER_ID_KEY, userId)
                }
            }
    }
}
