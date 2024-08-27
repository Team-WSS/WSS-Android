package com.teamwss.websoso.ui.profileEdit

import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.databinding.ActivityProfileEditBinding
import com.teamwss.websoso.ui.profileEdit.model.Genres

class ProfileEditActivity :
    BaseActivity<ActivityProfileEditBinding>(R.layout.activity_profile_edit) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupGenreChips()
    }

    private fun setupGenreChips() {
        Genres.entries.forEach { genre ->
            WebsosoChip(binding.root.context)
                .apply {
                    setWebsosoChipText(genre.krName)
                    setWebsosoChipTextAppearance(R.style.body2)
                    setWebsosoChipTextColor(R.color.bg_profile_edit_chip_text_selector)
                    setWebsosoChipStrokeColor(R.color.bg_profile_edit_chip_stroke_selector)
                    setWebsosoChipBackgroundColor(R.color.bg_profile_edit_chip_background_selector)
                    setWebsosoChipPaddingVertical(20f)
                    setWebsosoChipPaddingHorizontal(12f)
                    setWebsosoChipRadius(40f)
                }.also { websosoChip -> binding.wcgProfileEditPreferGenre.addChip(websosoChip) }
        }
    }
}
