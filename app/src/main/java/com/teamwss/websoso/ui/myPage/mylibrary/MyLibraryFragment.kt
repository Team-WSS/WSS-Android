package com.teamwss.websoso.ui.myPage.mylibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.AttractivePointData
import com.teamwss.websoso.data.model.GenrePreferredEntity
import com.teamwss.websoso.databinding.FragmentMyLibraryBinding
import com.teamwss.websoso.ui.common.customView.WebsosoChip

class MyLibraryFragment : Fragment() {
    private var _binding: FragmentMyLibraryBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel: MyLibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        observeViewModel()
    }

    private fun initializeViews() {
        initializeAttractivePoints()
        initializeGenrePathToggle()
    }

    private fun initializeAttractivePoints() {
        libraryViewModel.attractivePoints.observe(viewLifecycleOwner) { attractivePoints ->
            binding.cgAttractivePoints.removeAllViews()

            for (data in attractivePoints) {
                val chip = createChip(data)
                binding.cgAttractivePoints.addView(chip)
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

    private fun initializeGenrePathToggle() {
        binding.ivPreferredGenrePath.setOnClickListener {
            libraryViewModel.toggleGenreListVisibility()
        }
    }

    private fun observeViewModel() {
        libraryViewModel.genres.observe(viewLifecycleOwner) { genres ->
            updateGenreBottomList(genres)
        }

        libraryViewModel.isGenreListVisible.observe(viewLifecycleOwner) { isVisible ->
            updateRestPreferredGenreVisibility(isVisible)
        }

        libraryViewModel.attractivePoints.observe(viewLifecycleOwner) { attractivePoints ->
            updateAttractivePoints(attractivePoints)
        }
    }

    private fun updateGenreBottomList(genres: List<GenrePreferredEntity>) {
        val adapter =
            RestPreferredGenreAdapter(requireContext(), R.layout.item_rest_preferred_genre, genres)
        binding.listRestPreferredGenre.adapter = adapter
        adjustListViewHeight(binding.listRestPreferredGenre)
    }

    private fun adjustListViewHeight(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0

        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }

    private fun updateRestPreferredGenreVisibility(isVisible: Boolean) {
        binding.listRestPreferredGenre.isVisible = isVisible
        binding.ivPreferredGenrePath.setImageResource(
            if (isVisible) R.drawable.ic_upper_path else R.drawable.ic_lower_path
        )
    }

    private fun updateAttractivePoints(attractivePoints: List<AttractivePointData>) {
        binding.cgAttractivePoints.removeAllViews()
        attractivePoints.forEach { data ->
            val attractiveChip = createChip(data)
            binding.cgAttractivePoints.addView(attractiveChip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}