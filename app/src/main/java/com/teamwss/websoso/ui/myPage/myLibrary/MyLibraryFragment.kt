package com.teamwss.websoso.ui.myPage.myLibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.databinding.FragmentMyLibraryBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyLibraryFragment : BindingFragment<FragmentMyLibraryBinding>(R.layout.fragment_my_library) {
    private val myLibraryViewModel: MyLibraryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.myLibraryViewModel= myLibraryViewModel

        onGenrePathToggled()
        setUpObserve()
    }

    private fun onGenrePathToggled() {
        binding.ivMyLibraryGenrePreferencePath.setOnClickListener {
            myLibraryViewModel.toggleGenreListVisibility()
        }
    }

    private fun setUpObserve() {
        myLibraryViewModel.genres.observe(viewLifecycleOwner) { genres ->
            updateRestPreferredGenreList(genres)
        }

        myLibraryViewModel.isGenreListVisible.observe(viewLifecycleOwner) { isVisible ->
            updateRestGenrePreferenceVisibility(isVisible)
        }
    }

    private fun updateRestPreferredGenreList(genres: List<GenrePreferenceEntity>) {
        val adapter = RestGenrePreferenceAdapter(genres)
        binding.lvMyLibraryRestGenre.adapter = adapter
    }

    private fun updateRestGenrePreferenceVisibility(isVisible: Boolean) {
        binding.lvMyLibraryRestGenre.isVisible = isVisible
        binding.ivMyLibraryGenrePreferencePath.setImageResource(
            if (isVisible) R.drawable.ic_upper_path else R.drawable.ic_lower_path
        )
    }
}