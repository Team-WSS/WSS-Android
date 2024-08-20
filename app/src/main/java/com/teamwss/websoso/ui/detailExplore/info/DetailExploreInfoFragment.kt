package com.teamwss.websoso.ui.detailExplore.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentDetailExploreInfoBinding
import com.teamwss.websoso.common.ui.base.BindingFragment
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.ui.detailExplore.DetailExploreViewModel
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExplore.info.model.Rating
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreInfoFragment :
    BindingFragment<FragmentDetailExploreInfoBinding>(R.layout.fragment_detail_explore_info) {
    private val detailExploreViewModel: DetailExploreViewModel by activityViewModels()
    private val detailExploreInfoViewModel: DetailExploreInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setupGenreChips()
        setupSeriesStatusChips()
        setupRatingChips()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.detailExploreInfoviewModel = detailExploreInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun setupGenreChips() {
        val genres = Genre.entries
        genres.forEach { genre ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(genre.title)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_detail_explore_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_detail_explore_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_detail_explore_chip_background_selector)
                setWebsosoChipPaddingVertical(30f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(45f)
                setOnWebsosoChipClick { detailExploreViewModel.updateSelectedGenres(genre.title) }
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
                        detailExploreViewModel.updateSelectedSeriesStatus(chip.text.toString())
                    }

                    false -> detailExploreViewModel.updateSelectedSeriesStatus(null)
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
        detailExploreViewModel.selectedStatus.observe(viewLifecycleOwner) { selectedStatus ->
            listOf(
                binding.chipDetailExploreInfoStatusInSeries,
                binding.chipDetailExploreInfoStatusComplete,
            ).forEach { chip ->
                chip.isChecked = selectedStatus == chip.text.toString()
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