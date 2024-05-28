package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelRatingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class NovelRatingActivity :
    BindingActivity<ActivityNovelRatingBinding>(R.layout.activity_novel_rating) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvNovelRatingAddDate.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingDateDialog")
        if (existingDialog == null) {
            val bottomSheetDialog = RatingDateDialog()
            bottomSheetDialog.show(supportFragmentManager, "RatingDateDialog")
        }
    }
}