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
    private var _binding: FragmentMyLibraryBinding? = null
    private val myLibraryBinding get() = _binding ?: error("error: myLibraryBinding is null")
    private val myLibraryViewModel: MyLibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyLibraryBinding.inflate(inflater, container, false)
        return myLibraryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onGenrePathToggled()
        setUpObserve()
    }

    private fun onGenrePathToggled() {
        myLibraryBinding.ivMyLibraryGenrePreferencePath.setOnClickListener {
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
        myLibraryBinding.lvMyLibraryRestGenre.adapter = adapter
    }

    private fun updateRestGenrePreferenceVisibility(isVisible: Boolean) {
        myLibraryBinding.lvMyLibraryRestGenre.isVisible = isVisible
        myLibraryBinding.ivMyLibraryGenrePreferencePath.setImageResource(
            if (isVisible) R.drawable.ic_upper_path else R.drawable.ic_lower_path
        )
    }
}