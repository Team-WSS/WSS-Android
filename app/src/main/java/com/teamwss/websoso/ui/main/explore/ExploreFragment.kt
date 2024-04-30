package com.teamwss.websoso.ui.main.explore

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentExploreBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.main.explore.adapter.SosoPickAdapter

class ExploreFragment : BindingFragment<FragmentExploreBinding>(R.layout.fragment_explore) {
    private val sosoPickAdapter: SosoPickAdapter by lazy { SosoPickAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSosoPickAdapter()
    }

    private fun initSosoPickAdapter() {
        binding.rvExploreSosoPick.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sosoPickAdapter
            setHasFixedSize(true)
        }
    }
}