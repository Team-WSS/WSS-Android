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
import com.teamwss.websoso.data.model.AttractivePointEntity
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

        myLibraryViewModel.attractivePoints.observe(viewLifecycleOwner) { attractivePoints ->
            updateAttractivePoints(attractivePoints)
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

        val attractivePointTextLength = attractivePointText.indexOf(getString(R.string.my_library_attractive_point_fixed_text))
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

    private fun updateAttractivePoints(attractivePoints: List<AttractivePointEntity>) {
        binding.cgMyLibraryAttractivePoints.removeAllViews()
        attractivePoints.forEach { data ->
            val attractiveChip = createChip(data)
            binding.cgMyLibraryAttractivePoints.addView(attractiveChip)
        }
    }

    private fun createChip(data: AttractivePointEntity): Chip {
        val chip = WebsosoChip(requireContext())
        chip.text = "${data.attractivePoint} ${data.pointCount}"
        chip.isCheckable = true
        chip.isChecked = false

        chip.setChipBackgroundColorResource(R.color.primary_50_F1EFFF)
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_100_6A5DFD))
        chip.setTextAppearance(R.style.body2)

        return chip
    }
}