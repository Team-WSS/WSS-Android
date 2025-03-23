package com.into.websoso.ui.profileEdit.model

import com.into.websoso.resource.R.raw.lottie_regressor_0
import com.into.websoso.resource.R.raw.lottie_regressor_1
import com.into.websoso.resource.R.raw.lottie_sosocat_0
import com.into.websoso.resource.R.raw.lottie_sosocat_1
import com.into.websoso.resource.R.raw.lottie_villainess_0
import com.into.websoso.resource.R.raw.lottie_villainess_1

enum class Avatar(val avatarId: Int, val firstAnimationId: Int, val secondAnimationId: Int) {
    SOSOCAT(1, lottie_sosocat_0, lottie_sosocat_1),
    REGRESSOR(2, lottie_regressor_0, lottie_regressor_1),
    VILLAINESS(3, lottie_villainess_0, lottie_villainess_1),
    ;

    companion object {
        private const val FIRST_AVATAR_ANIMATION = 0
        private const val SECOND_AVATAR_ANIMATION = 1

        fun AvatarModel.animation(): Int {
            val random = (0..1).random()

            return entries.first { it.avatarId == avatarId }.let {
                when (random) {
                    FIRST_AVATAR_ANIMATION -> it.firstAnimationId
                    SECOND_AVATAR_ANIMATION -> it.secondAnimationId
                    else -> 0
                }
            }
        }
    }
}
