package com.teamwss.websoso.ui.myPage.myLibrary

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.databinding.FragmentMyLibraryBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.myPage.myLibrary.adapter.RestGenrePreferenceAdapter
import com.teamwss.websoso.util.setListViewHeightBasedOnChildren
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyLibraryFragment : BindingFragment<FragmentMyLibraryBinding>(R.layout.fragment_my_library) {
    private val myLibraryViewModel: MyLibraryViewModel by viewModels()
    private val restGenrePreferenceAdapter: RestGenrePreferenceAdapter by lazy {
        RestGenrePreferenceAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.myLibraryViewModel = myLibraryViewModel

        setupRestGenrePreferenceAdapter()
        setUpObserve()
    }

    private fun setupRestGenrePreferenceAdapter() {
        binding.lvMyLibraryRestGenre.adapter = restGenrePreferenceAdapter
    }

    private fun setUpObserve() {
        myLibraryViewModel.genres.observe(viewLifecycleOwner) { genres ->
            restGenrePreferenceAdapter.updateRestGenrePreferenceData(genres)
        }

        myLibraryViewModel.isGenreListVisible.observe(viewLifecycleOwner) { isVisible ->
            updateRestGenrePreferenceVisibility(isVisible)
        }

        myLibraryViewModel.attractivePointsText.observe(viewLifecycleOwner) { combinedText ->
            val primary100 = ContextCompat.getColor(requireContext(), R.color.primary_100_6A5DFD)
            val gray300 = ContextCompat.getColor(requireContext(), R.color.gray_300_52515F)
            applyTextColors(binding.tvMyLibraryAttractivePoints, combinedText, primary100, gray300)
        }

        myLibraryViewModel.novelPreferences.observe(viewLifecycleOwner) { novelPreferences ->
            updateNovelPreferencesKeywords(novelPreferences)
        }
    }

    private fun updateRestGenrePreferenceVisibility(isVisible: Boolean) {
        binding.lvMyLibraryRestGenre.isVisible = isVisible
        if (isVisible) {
            binding.lvMyLibraryRestGenre.setListViewHeightBasedOnChildren()
        }
    }

    private fun applyTextColors(
        attractivePoint: TextView,
        attractivePointText: String,
        attractivePointTextColor: Int,
        fixedTextColor: Int
    ) {
        val spannableStringBuilder = SpannableStringBuilder()

        val attractivePointTextLength = attractivePointText.indexOf("가 매력적인 작품")
        val attractivePoints = SpannableString(attractivePointText.substring(0, attractivePointTextLength)).apply {
            setSpan(ForegroundColorSpan(attractivePointTextColor), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        spannableStringBuilder.append(attractivePoints)

        val fixedSpannable = SpannableString(attractivePointText.substring(attractivePointTextLength)).apply {
            setSpan(ForegroundColorSpan(fixedTextColor), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        spannableStringBuilder.append(fixedSpannable)

        attractivePoint.text = spannableStringBuilder
    }

    private fun updateNovelPreferencesKeywords(novelPreferences: NovelPreferenceEntity) {
        novelPreferences.keywords.forEach { keyword ->
            val chip = createKeywordChip(keyword)
            binding.wcgMyLibraryAttractivePoints.addView(chip)
        }
    }

    private fun createKeywordChip(data: NovelPreferenceEntity.KeywordEntity): Chip {
        return WebsosoChip(requireContext()).apply {
            text = "${data.keywordName} ${data.keywordCount}"
            isCheckable = true
            isChecked = false

            setChipBackgroundColorResource(R.color.primary_50_F1EFFF)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_100_6A5DFD))
            setTextAppearance(R.style.body2)
        }
    }
}
