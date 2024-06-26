package com.teamwss.websoso.ui.onboarding.second

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentOnboardingSecondBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingSecondFragment :
    BindingFragment<FragmentOnboardingSecondBinding>(R.layout.fragment_onboarding_second) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}