package com.teamwss.websoso.ui.explore

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentExploreBinding
import com.teamwss.websoso.ui.common.base.BindingFragment


class ExploreFragment : BindingFragment<FragmentExploreBinding>(R.layout.fragment_explore) {
    private lateinit var sosoAdapter: SosoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSosoAdapter()
    }

    private fun initSosoAdapter() {
        sosoAdapter = SosoAdapter()
        binding.rvExploreSosoPick.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sosoAdapter
        }
    }
}