package com.teamwss.websoso.ui.myPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentMyPageBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private var _binding: FragmentMyPageBinding? = null
    private val myPageBinding get() = _binding ?: error("error: myPageBinding is null")
    private val myPageViewModel: MyPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return myPageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPageBinding.viewModel = myPageViewModel
        setupViewPagerAndTabs()
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = MyViewPagerAdapter(childFragmentManager, lifecycle)
        myPageBinding.vpMyPage.adapter = pagerAdapter

        TabLayoutMediator(myPageBinding.tbMyPage, myPageBinding.vpMyPage) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.my_page_library)
                1 -> tab.text = getString(R.string.my_page_feed)
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
