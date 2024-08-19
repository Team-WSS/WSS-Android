package com.teamwss.websoso.ui.onboarding.third


import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentOnboardingThirdBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.onboarding.OnboardingViewModel
import com.teamwss.websoso.ui.onboarding.third.adapter.GenreAdapter
import com.teamwss.websoso.ui.onboarding.third.model.Genre
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingThirdFragment :
    BindingFragment<FragmentOnboardingThirdBinding>(R.layout.fragment_onboarding_third) {
    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    private lateinit var adapter: GenreAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGenreAdapter()
        setupObserver()

        binding.viewModel = onboardingViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupGenreAdapter() {
        adapter = GenreAdapter(
            onboardingViewModel::updateGenreSelection,
            onboardingViewModel::isGenreSelected
        )
        binding.rvOnboardingThird.adapter = adapter
        adapter.submitList(Genre.entries.toList())
    }

    private fun setupObserver() {
        onboardingViewModel.selectedGenres.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }
}

