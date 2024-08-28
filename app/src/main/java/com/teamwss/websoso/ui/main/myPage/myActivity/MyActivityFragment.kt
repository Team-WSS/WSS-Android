package com.teamwss.websoso.ui.main.myPage.myActivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentMyActivityBinding
import com.teamwss.websoso.ui.myActivityDetail.MyActivityDetailActivity
import com.teamwss.websoso.ui.main.myPage.myActivity.adapter.MyActivityAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivityFragment :
    BaseFragment<FragmentMyActivityBinding>(R.layout.fragment_my_activity) {
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val myActivityAdapter: MyActivityAdapter by lazy {
        MyActivityAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMyActivitiesAdapter()
        setUpObserve()
        navigateToMyActivityDetail()
    }

    private fun setUpMyActivitiesAdapter() {
        binding.rvMyActivity.adapter = myActivityAdapter
    }

    private fun setUpObserve() {
        myActivityViewModel.myActivity.observe(viewLifecycleOwner) { activities ->
            myActivityAdapter.submitList(activities)
        }
    }

    private fun navigateToMyActivityDetail() {
        binding.btnMyActivityMore.setOnClickListener {
            val intent = MyActivityDetailActivity.createIntentForMyActivityDetail(requireContext())
            startActivity(intent)
        }
    }
}