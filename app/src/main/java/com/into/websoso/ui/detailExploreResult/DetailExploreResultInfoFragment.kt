package com.into.websoso.ui.detailExploreResult

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.databinding.FragmentDetailExploreResultInfoBinding
import com.into.websoso.ui.detailExplore.info.model.Genre
import com.into.websoso.ui.detailExplore.info.model.Rating
import com.into.websoso.ui.detailExplore.info.model.SeriesStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreResultInfoFragment :
    BaseFragment<FragmentDetailExploreResultInfoBinding>(R.layout.fragment_detail_explore_result_info) {
    private val detailExploreResultViewModel: DetailExploreResultViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        onResetButtonClick()
        onDetailSearchNovelButtonClick()
        setupGenreChips()
        setupSeriesStatusChips()
        setupRatingChips()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.detailExploreResultViewModel = detailExploreResultViewModel
        binding.lifecycleOwner = this
    }

    private fun onResetButtonClick() {
        binding.clDetailExploreInfoResetButton.setOnClickListener {
            detailExploreResultViewModel.updateSelectedInfoValueClear()
        }
    }

    private fun onDetailSearchNovelButtonClick() {
        binding.tvDetailExploreSearchButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                detailExploreResultViewModel.updateSearchResult(isSearchButtonClick = true)

                (parentFragment as? DetailExploreResultDialogBottomSheet)?.dismiss()

                detailExploreResultViewModel.updateIsBottomSheetOpen(false)
            }
        }
    }

    private fun setupGenreChips() {
        val genres = Genre.entries
        genres.forEach { genre ->
            WebsosoChip(requireContext())
                .apply {
                    setWebsosoChipText(genre.titleKr)
                    setWebsosoChipTextAppearance(R.style.body2)
                    setWebsosoChipTextColor(R.color.bg_detail_explore_chip_text_selector)
                    setWebsosoChipStrokeColor(R.color.bg_detail_explore_chip_stroke_selector)
                    setWebsosoChipBackgroundColor(R.color.bg_detail_explore_chip_background_selector)
                    setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                    setWebsosoChipPaddingHorizontal(6f.toFloatPxFromDp())
                    setWebsosoChipRadius(20f.toFloatPxFromDp())
                    setOnWebsosoChipClick { detailExploreResultViewModel.updateSelectedGenres(genre) }
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
                        detailExploreResultViewModel.updateSelectedSeriesStatus(status)
                    }

                    false -> {
                        detailExploreResultViewModel.updateSelectedSeriesStatus(null)
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
                        detailExploreResultViewModel.updateSelectedRating(ratingValue?.value)
                    }

                    false -> detailExploreResultViewModel.updateSelectedRating(null)
                }
            }
        }
    }

    private fun setupChipCheckListener(
        chip: Chip,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        chip.setOnCheckedChangeListener(null)
        chip.setOnCheckedChangeListener { _, isChecked -> onCheckedChange(isChecked) }
    }

    private fun setupObserver() {
        detailExploreResultViewModel.selectedGenres.observe(viewLifecycleOwner) { genres ->
            (0 until binding.wcgDetailExploreInfoGenre.childCount)
                .map { binding.wcgDetailExploreInfoGenre.getChildAt(it) }
                .filterIsInstance<WebsosoChip>()
                .forEach { chip ->
                    chip.isSelected = genres?.any { it.titleKr == chip.text } == true
                }
        }

        detailExploreResultViewModel.isNovelCompleted.observe(viewLifecycleOwner) { selectedStatus ->
            val selectedChip = selectedStatus?.let {
                SeriesStatus.fromIsCompleted(it).title
            }

            listOf(
                binding.chipDetailExploreInfoStatusInSeries,
                binding.chipDetailExploreInfoStatusComplete,
            ).forEach { chip ->
                chip.isChecked = chip.text.toString() == selectedChip
            }
        }

        detailExploreResultViewModel.selectedRating.observe(viewLifecycleOwner) { selectedRating ->
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
