package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentOtherUserActivityBinding
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity
import com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter.OtherUserActivityAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserActivityFragment :
    BaseFragment<FragmentOtherUserActivityBinding>(R.layout.fragment_other_user_activity) {
    private val otherUserActivityViewModel: OtherUserActivityViewModel by viewModels()
    private val otherUserActivityAdapter: OtherUserActivityAdapter by lazy {
        OtherUserActivityAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMyActivitiesAdapter()
        setUpObserve()
        navigateToMyActivityDetail()
    }

    private fun setUpMyActivitiesAdapter() {
        binding.rvOtherUserActivity.adapter = otherUserActivityAdapter
    }

    private fun setUpObserve() {
        otherUserActivityViewModel.otherUserActivity.observe(viewLifecycleOwner) { activities ->
            otherUserActivityAdapter.submitList(activities)
        }
    }

    private fun navigateToMyActivityDetail() {
        binding.btnOtherUserActivityMore.setOnClickListener {
            val intent = ActivityDetailActivity.createIntentForMyActivityDetail(requireContext())
            startActivity(intent)
        }
    }
}

