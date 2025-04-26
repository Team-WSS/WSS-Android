package com.into.websoso.ui.main.myPage.myLibrary

import android.app.Activity
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.chip.Chip
import com.into.websoso.R.color.gray_300_52515F
import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.color.primary_50_F1EFFF
import com.into.websoso.R.layout.fragment_my_library
import com.into.websoso.R.style.body2
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.setListViewHeightBasedOnChildren
import com.into.websoso.core.resource.R.string.my_library_attractive_point_fixed_text
import com.into.websoso.data.model.GenrePreferenceEntity
import com.into.websoso.data.model.NovelPreferenceEntity
import com.into.websoso.databinding.FragmentMyLibraryBinding
import com.into.websoso.ui.main.myPage.myLibrary.adapter.RestGenrePreferenceAdapter
import com.into.websoso.ui.userStorage.UserStorageActivity
import com.into.websoso.ui.userStorage.model.StorageTab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyLibraryFragment : BaseFragment<FragmentMyLibraryBinding>(fragment_my_library) {
    private val myLibraryViewModel: MyLibraryViewModel by viewModels()
    private val restGenrePreferenceAdapter: RestGenrePreferenceAdapter by lazy {
        RestGenrePreferenceAdapter()
    }
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val userStorageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                myLibraryViewModel.updateMyLibrary()
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.myLibraryViewModel = myLibraryViewModel

        setupRestGenrePreferenceAdapter()
        setUpObserve()
        onStorageButtonClick()
    }

    private fun setupRestGenrePreferenceAdapter() {
        binding.lvMyLibraryRestGenre.adapter = restGenrePreferenceAdapter
    }

    private fun setUpObserve() {
        myLibraryViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.isLoading -> binding.wllMyLibrary.setWebsosoLoadingVisibility(true)
                uiState.isError -> binding.wllMyLibrary.setLoadingLayoutVisibility(false)
                !uiState.isLoading -> {
                    binding.wllMyLibrary.setWebsosoLoadingVisibility(false)
                }
            }

            when (myLibraryViewModel.hasNoPreferences()) {
                true -> {
                    binding.clMyLibraryKnownPreference.visibility = View.GONE
                    binding.clMyLibraryUnknownPreference.visibility = View.VISIBLE
                }

                false -> {
                    binding.clMyLibraryKnownPreference.visibility = View.VISIBLE
                    binding.clMyLibraryUnknownPreference.visibility = View.GONE
                }
            }

            when (myLibraryViewModel.hasNovelPreferences()) {
                true -> {
                    binding.clMyLibraryNovelPreference.visibility = View.VISIBLE
                    binding.clMyLibraryUnknownNovelPreference.visibility = View.GONE
                }

                false -> {
                    binding.clMyLibraryNovelPreference.visibility = View.GONE
                    binding.clMyLibraryUnknownNovelPreference.visibility = View.VISIBLE
                }
            }

            when (myLibraryViewModel.hasAttractivePoints()) {
                true -> {
                    binding.clMyLibraryAttractivePoints.visibility = View.VISIBLE
                }

                false -> {
                    binding.clMyLibraryAttractivePoints.visibility = View.GONE
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

    private fun onStorageButtonClick() {
        val tabClickMappings = mapOf(
            binding.clMyLibraryTopBar to StorageTab.INTEREST.readStatus,
            binding.llMyLibraryStorageInteresting to StorageTab.INTEREST.readStatus,
            binding.llMyLibraryStorageWatching to StorageTab.WATCHING.readStatus,
            binding.llMyLibraryStorageWatched to StorageTab.WATCHED.readStatus,
            binding.llMyLibraryStorageQuit to StorageTab.QUIT.readStatus,
        )

        tabClickMappings.forEach { (view, readStatus) ->
            view.setOnClickListener {
                singleEventHandler.throttleFirst {
                    navigateToStorageActivity(readStatus)
                }
            }
        }
    }

    private fun navigateToStorageActivity(readStatus: String) {
        userStorageResultLauncher.launch(
            UserStorageActivity.getIntent(
                context = requireContext(),
                source = UserStorageActivity.SOURCE_MY_LIBRARY,
                userId = myLibraryViewModel.userId,
                readStatus = readStatus,
            ),
        )
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

    override fun onResume() {
        super.onResume()

        view?.requestLayout()
    }

    companion object {
        const val EXTRA_SOURCE = "source"
    }
}
