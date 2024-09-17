package com.teamwss.websoso.ui.otherUserPage.otherUserLibrary

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.util.setListViewHeightBasedOnChildren
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.databinding.FragmentOtherUserLibraryBinding
import com.teamwss.websoso.ui.otherUserPage.otherUserLibrary.adapter.RestGenrePreferenceAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserLibraryFragment :
    BaseFragment<FragmentOtherUserLibraryBinding>(R.layout.fragment_other_user_library) {
    private val otherUserLibraryViewModel: OtherUserLibraryViewModel by viewModels()
    private val restGenrePreferenceAdapter: RestGenrePreferenceAdapter by lazy {
        RestGenrePreferenceAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.otherUserLibraryViewModel = otherUserLibraryViewModel

        setUpRestGenrePreferenceAdapter()
        setUpObserve()
    }

    private fun setUpRestGenrePreferenceAdapter() {
        binding.lvOtherUserLibraryRestGenre.adapter = restGenrePreferenceAdapter
    }

    private fun setUpObserve() {
        otherUserLibraryViewModel.restGenres.observe(viewLifecycleOwner) { genres ->
            restGenrePreferenceAdapter.updateRestGenrePreferenceData(genres)
        }

        otherUserLibraryViewModel.isGenreListVisible.observe(viewLifecycleOwner) { isVisible ->
            updateRestGenrePreferenceVisibility(isVisible)
        }

        otherUserLibraryViewModel.translatedAttractivePoints.observe(viewLifecycleOwner) { translatedPoints ->
            val combinedText =
                translatedPoints.joinToString(", ") + getString(R.string.my_library_attractive_point_fixed_text)
            applyTextColors(combinedText)
        }

        otherUserLibraryViewModel.novelPreferences.observe(viewLifecycleOwner) { novelPreferences ->
            updateNovelPreferencesKeywords(novelPreferences)
        }
    }

    private fun updateRestGenrePreferenceVisibility(isVisible: Boolean) {
        binding.lvOtherUserLibraryRestGenre.isVisible = isVisible
        if (isVisible) {
            binding.lvOtherUserLibraryRestGenre.setListViewHeightBasedOnChildren()
        }
    }

    private fun applyTextColors(combinedText: String) {
        val primary100 = requireContext().getColor(R.color.primary_100_6A5DFD)
        val gray300 = requireContext().getColor(R.color.gray_300_52515F)

        val spannableStringBuilder = SpannableStringBuilder()

        val fixedText = getString(R.string.my_library_attractive_point_fixed_text)

        val splitText = combinedText.split(fixedText)

        if (splitText.isNotEmpty()) {
            val attractivePoints =
                SpannableString(splitText[0]).apply {
                    setSpan(
                        ForegroundColorSpan(primary100),
                        0,
                        length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            spannableStringBuilder.append(attractivePoints)

            val fixedSpannable =
                SpannableString(fixedText).apply {
                    setSpan(
                        ForegroundColorSpan(gray300),
                        0,
                        length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            spannableStringBuilder.append(fixedSpannable)
        } else {
            val spannable = SpannableString(combinedText).apply {
                setSpan(
                    ForegroundColorSpan(primary100),
                    0,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
            spannableStringBuilder.append(spannable)
        }

        binding.tvOtherUserLibraryAttractivePoints.text = spannableStringBuilder
    }

    private fun updateNovelPreferencesKeywords(novelPreferences: NovelPreferenceEntity) {
        novelPreferences.keywords.forEach { keyword ->
            val chip = createKeywordChip(keyword)
            binding.wcgOtherUserLibraryAttractivePoints.addView(chip)
        }
    }

    private fun createKeywordChip(data: NovelPreferenceEntity.KeywordEntity): Chip {
        return WebsosoChip(requireContext()).apply {
            text = "${data.keywordName} ${data.keywordCount}"
            isChecked = false

            setChipBackgroundColorResource(R.color.primary_50_F1EFFF)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_100_6A5DFD))
            setTextAppearance(R.style.body2)
        }
    }
}
