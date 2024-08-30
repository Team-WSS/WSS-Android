package com.teamwss.websoso.ui.detailExplore.info

import android.os.Bundle
import android.view.View
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.util.toFloatScaledByPx
import com.teamwss.websoso.databinding.FragmentDetailExploreInfoBinding
import com.teamwss.websoso.ui.detailExplore.DetailExploreViewModel
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExplore.info.model.Rating
import com.teamwss.websoso.ui.detailExplore.info.model.SeriesStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreInfoFragment :
    BaseFragment<FragmentDetailExploreInfoBinding>(R.layout.fragment_detail_explore_info) {
    private val detailExploreViewModel: DetailExploreViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        onResetButtonClick()
        setupGenreChips()
        setupSeriesStatusChips()
        setupRatingChips()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.detailExploreViewModel = detailExploreViewModel
        binding.lifecycleOwner = this
    }

    private fun onResetButtonClick() {
        binding.clDetailExploreInfoResetButton.setOnClickListener {
            detailExploreViewModel.updateSelectedInfoValueClear()
        }
    }

    private fun setupGenreChips() {
        val genres = Genre.entries
        genres.forEach { genre ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(genre.titleKr)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_detail_explore_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_detail_explore_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_detail_explore_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatScaledByPx())
                setWebsosoChipPaddingHorizontal(6f.toFloatScaledByPx())
                setWebsosoChipRadius(20f.toFloatScaledByPx())
                setOnWebsosoChipClick { detailExploreViewModel.updateSelectedGenres(genre) }
            }.also { websosoChip -> binding.wcgDetailExploreInfoGenre.addChip(websosoChip) }
        }
    }

    private fun setupSeriesStatusChips() {
        val seriesStatusChips = listOf(
            binding.chipDetailExploreInfoStatusInSeries,
            binding.chipDetailExploreInfoStatusComplete,
        )

        seriesStatusChips.forEach { chip ->
            setupChipCheckListener(chip) { isChecked ->
                when (isChecked) {
                    true -> {
                        seriesStatusChips.filter { it != chip }.forEach { it.isChecked = false }

                        val status = SeriesStatus.from(chip.text.toString())
                        detailExploreViewModel.updateSelectedSeriesStatus(status)
                    }

                    false -> {
                        detailExploreViewModel.updateSelectedSeriesStatus(null)
                    }

                }
            }
        }
    }

    private fun setupRatingChips() {
        val ratingChips = listOf(
            binding.chipDetailExploreInfoRatingLowest,
            binding.chipDetailExploreInfoRatingLower,
            binding.chipDetailExploreInfoRatingHigher,
            binding.chipDetailExploreInfoRatingHighest,
        )

        ratingChips.forEach { chip ->
            setupChipCheckListener(chip) { isChecked ->
                when (isChecked) {
                    true -> {
                        ratingChips.filter { it != chip }.forEach { it.isChecked = false }
                        val ratingValue =
                            Rating.entries.find {
                                chip.text.toString().contains(it.value.toString())
                            }
                        detailExploreViewModel.updateSelectedRating(ratingValue?.value)
                    }

                    false -> detailExploreViewModel.updateSelectedRating(null)
                }
            }
        }
    }

    private fun setupChipCheckListener(chip: Chip, onCheckedChange: (Boolean) -> Unit) {
        chip.setOnCheckedChangeListener(null)
        chip.isChecked = false
        chip.setOnCheckedChangeListener { _, isChecked -> onCheckedChange(isChecked) }
    }

    private fun setupObserver() {
        detailExploreViewModel.selectedGenres.observe(viewLifecycleOwner) { genres ->
            if (genres.isNullOrEmpty()) binding.wcgDetailExploreInfoGenre.forEach {
                it.isSelected = false
            }
        }

        detailExploreViewModel.selectedStatus.observe(viewLifecycleOwner) { selectedStatus ->
            val selectedChip = selectedStatus?.title

            listOf(
                binding.chipDetailExploreInfoStatusInSeries,
                binding.chipDetailExploreInfoStatusComplete,
            ).forEach { chip ->
                chip.isChecked = chip.text.toString() == selectedChip
            }
        }

        detailExploreViewModel.selectedRating.observe(viewLifecycleOwner) { selectedRating ->
            listOf(
                binding.chipDetailExploreInfoRatingLowest,
                binding.chipDetailExploreInfoRatingLower,
                binding.chipDetailExploreInfoRatingHigher,
                binding.chipDetailExploreInfoRatingHighest,
            ).forEach { chip ->
                val ratingValue =
                    Rating.entries.find { chip.text.toString().contains(it.value.toString()) }
                chip.isChecked = selectedRating == ratingValue?.value
            }
        }
    }
}
