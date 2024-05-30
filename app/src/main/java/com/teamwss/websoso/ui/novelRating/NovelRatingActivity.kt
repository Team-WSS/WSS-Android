package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelRatingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class NovelRatingActivity :
    BindingActivity<ActivityNovelRatingBinding>(R.layout.activity_novel_rating) {
    private val viewModel: NovelRatingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBinding()
        viewModel.getUserNovelInfo()
        observeDisplayDate()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
    }

    private fun observeDisplayDate() {
        viewModel.uiState.observe(this) {
            val startDate: Triple<Int, Int, Int>? = it.novelRatingModel.currentStartDate
            val endDate: Triple<Int, Int, Int>? = it.novelRatingModel.currentEndDate

            val text = when {
                startDate == null && endDate == null -> {
                    getString(R.string.rating_add_date)
                }

                startDate != null && endDate != null -> {
                    getString(
                        R.string.rating_display_date_with_tilde,
                        startDate.first, startDate.second, startDate.third,
                        endDate.first, endDate.second, endDate.third
                    )
                }

                startDate != null -> {
                    getString(
                        R.string.rating_display_date,
                        startDate.first, startDate.second, startDate.third
                    )
                }

                endDate != null -> {
                    getString(
                        R.string.rating_display_date,
                        endDate.first, endDate.second, endDate.third
                    )
                }

                else -> ""
            }

            val spannableString = SpannableString(text).apply {
                setSpan(UnderlineSpan(), 0, text.length, 0)
            }
            binding.tvNovelRatingDisplayDate.text = spannableString
        }
    }

    fun showDatePickerBottomSheet() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingDateDialog")
        if (existingDialog == null) {
            RatingDateDialog().show(supportFragmentManager, "RatingDateDialog")
        }
    }

    fun navigateToNovelDetail() {
        finish()
    }
}
