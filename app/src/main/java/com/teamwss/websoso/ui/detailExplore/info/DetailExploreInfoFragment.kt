package com.teamwss.websoso.ui.detailExplore.info

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentDetailExploreInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.detailExplore.info.model.Genre

class DetailExploreInfoFragment :
    BindingFragment<FragmentDetailExploreInfoBinding>(R.layout.fragment_detail_explore_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenreChips()
        setupRatingChipsText()
        setupStatusChipsSingleSelection()
        setupRatingChipsSingleSelection()
    }

    private fun setupGenreChips() {
        val genres = Genre.values().toList()
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
                setOnWebsosoChipClick { } // TODO 추후 추가 예정
            }.also { websosoChip -> binding.wcgDetailExploreInfoGenre.addChip(websosoChip) }
        }
    }

    private fun setupRatingChipsText() {
        val ratings = listOf(3.5f, 4.0f, 4.5f, 4.8f)

        with(binding) {
            chipDetailExploreInfoRatingLowest.text =
                requireContext().getString(R.string.detail_explore_info_rating_lowest, ratings[0])
            chipDetailExploreInfoRatingLower.text =
                requireContext().getString(R.string.detail_explore_info_rating_lower, ratings[1])
            chipDetailExploreInfoRatingHigher.text =
                requireContext().getString(R.string.detail_explore_info_rating_higher, ratings[2])
            chipDetailExploreInfoRatingHighest.text =
                requireContext().getString(R.string.detail_explore_info_rating_highest, ratings[3])
        }
    }

    private fun setupStatusChipsSingleSelection() {
        val statusChips = listOf(
            binding.chipDetailExploreInfoStatusInSeries,
            binding.chipDetailExploreInfoStatusComplete,
        )

        statusChips.forEach { chip ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    statusChips.filter { it != chip }.forEach { it.isChecked = false }
                }
            }
        }
    }

    private fun setupRatingChipsSingleSelection() {
        val ratingChips = listOf(
            binding.chipDetailExploreInfoRatingLowest,
            binding.chipDetailExploreInfoRatingLower,
            binding.chipDetailExploreInfoRatingHigher,
            binding.chipDetailExploreInfoRatingHighest,
        )

        ratingChips.forEach { chip ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    ratingChips.filter { it != chip }.forEach { it.isChecked = false }
                }
            }
        }
    }
}