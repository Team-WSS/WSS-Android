package com.teamwss.websoso.ui.explore

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentExploreBinding
import com.teamwss.websoso.ui.common.base.BindingFragment


class ExploreFragment : BindingFragment<FragmentExploreBinding>(R.layout.fragment_explore) {
    private lateinit var sosoPickAdapter: SosoPickAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSosoPickAdapter()
    }

    private fun initSosoPickAdapter() {
        sosoPickAdapter = SosoPickAdapter()
        binding.rvExploreSosoPick.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sosoPickAdapter
            addItemDecoration(SosoPickItemDecoration(SOSO_PICK_ITEM_RIGHT_OFFSET))
        }
    }

    companion object {
        private const val SOSO_PICK_ITEM_RIGHT_OFFSET: Int = 8
    }
}