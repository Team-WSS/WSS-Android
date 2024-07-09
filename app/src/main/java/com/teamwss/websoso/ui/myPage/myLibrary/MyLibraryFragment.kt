package com.teamwss.websoso.ui.myPage.myLibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.AttractivePointData
import com.teamwss.websoso.data.model.PreferredGenreEntity
import com.teamwss.websoso.databinding.FragmentMyLibraryBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyLibraryFragment : BindingFragment<FragmentMyLibraryBinding>(R.layout.fragment_my_library) {
    private var _binding: FragmentMyLibraryBinding? = null
    private val myLibraryBinding get() = _binding ?: error("error: myLibraryBinding is null")
    private val libraryViewModel: MyLibraryViewModel by viewModels()

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

        initializeViews()
        setUpObserveViewModel()
    }

    private fun initializeViews() {
        initializeAttractivePoints()
        onGenrePathToggled()
    }

    private fun initializeAttractivePoints() {
        libraryViewModel.attractivePoints.observe(viewLifecycleOwner) { attractivePoints ->
            myLibraryBinding.cgMyLibraryAttractivePoints.removeAllViews()

            for (data in attractivePoints) {
                val chip = createChip(data)
                myLibraryBinding.cgMyLibraryAttractivePoints.addView(chip)
            }
        }
    }

    private fun createChip(data: AttractivePointData): Chip {
        val chip = WebsosoChip(requireContext())
        chip.text = "${data.attractivePoint} ${data.pointCount}"
        chip.isCheckable = true
        chip.isChecked = false

        chip.setChipBackgroundColorResource(R.color.primary_50_F1EFFF)
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_100_6A5DFD))
        chip.setTextAppearance(R.style.body2)

        return chip
    }

    private fun onGenrePathToggled() {
        myLibraryBinding.ivMyLibraryPreferredGenrePath.setOnClickListener {
            libraryViewModel.toggleGenreListVisibility()
        }
    }

    private fun setUpObserveViewModel() {
        libraryViewModel.genres.observe(viewLifecycleOwner) { genres ->
            updateRestPreferredGenreList(genres)
        }

        libraryViewModel.isGenreListVisible.observe(viewLifecycleOwner) { isVisible ->
            updateRestPreferredGenreVisibility(isVisible)
        }

        libraryViewModel.attractivePoints.observe(viewLifecycleOwner) { attractivePoints ->
            updateAttractivePoints(attractivePoints)
        }

        libraryViewModel.genreCount.observe(viewLifecycleOwner) { genreCountString ->
            updateGenreCount(genreCountString)
        }
    }

    private fun updateRestPreferredGenreList(genres: List<PreferredGenreEntity>) {
        val adapter = RestPreferredGenreAdapter(genres)
        myLibraryBinding.listMyLibraryRestPreferredGenre.adapter = adapter
    }

    private fun updateRestPreferredGenreVisibility(isVisible: Boolean) {
        myLibraryBinding.listMyLibraryRestPreferredGenre.isVisible = isVisible
        myLibraryBinding.ivMyLibraryPreferredGenrePath.setImageResource(
            if (isVisible) R.drawable.ic_upper_path else R.drawable.ic_lower_path
        )
    }

    private fun updateAttractivePoints(attractivePoints: List<AttractivePointData>) {
        myLibraryBinding.cgMyLibraryAttractivePoints.removeAllViews()
        attractivePoints.forEach { data ->
            val attractiveChip = createChip(data)
            myLibraryBinding.cgMyLibraryAttractivePoints.addView(attractiveChip)
        }
    }

    private fun updateGenreCount(genreCount: String) {
        val adapter = myLibraryBinding.listMyLibraryRestPreferredGenre.adapter as? RestPreferredGenreAdapter
        adapter?.updateGenreCount(genreCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
