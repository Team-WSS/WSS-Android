package com.teamwss.websoso.ui.main.home

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentHomeBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>(R.layout.fragment_home){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}