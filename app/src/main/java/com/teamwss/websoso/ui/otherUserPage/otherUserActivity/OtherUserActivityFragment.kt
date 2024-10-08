package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentOtherUserActivityBinding
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.otherUserPage.OtherUserPageViewModel
import com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter.OtherUserActivityAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserActivityFragment :
    BaseFragment<FragmentOtherUserActivityBinding>(R.layout.fragment_other_user_activity) {
    private val otherUserActivityViewModel: OtherUserActivityViewModel by viewModels()
    private val otherUserPageViewModel: OtherUserPageViewModel by activityViewModels()
    private val otherUserActivityAdapter: OtherUserActivityAdapter by lazy {
        OtherUserActivityAdapter()
    }
    private var userId: Long = 0L
    private var userProfile: UserProfileModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserId()
        setUpMyActivitiesAdapter()
        setupObserve()
        onActivityDetailButtonClick()
    }

    private fun setupUserId() {
        userId = arguments?.getLong(USER_ID_KEY) ?: 0L
        otherUserActivityViewModel.updateUserId(userId)
    }

    private fun setUpMyActivitiesAdapter() {
        binding.rvOtherUserActivity.adapter = otherUserActivityAdapter
    }

    private fun setupObserve() {
        otherUserActivityViewModel.otherUserActivity.observe(viewLifecycleOwner) { activities ->
            otherUserActivityAdapter.submitList(activities)
        }

        otherUserPageViewModel.otherUserProfile.observe(viewLifecycleOwner) { otherUserProfile ->
            otherUserProfile?.let { otherUserProfileEntity ->
                val userProfile = UserProfileModel(
                    nickname = otherUserProfileEntity.nickname,
                    avatarImage = otherUserProfileEntity.avatarImage,
                )
                setupUserProfile(userProfile)
            }
        }
    }

    fun setupUserProfile(userProfile: UserProfileModel) {
        this.userProfile = userProfile
        otherUserActivityAdapter.setUserProfile(userProfile)
    }

    private fun onActivityDetailButtonClick() {
        binding.btnOtherUserActivityMore.setOnClickListener {
            val intent = ActivityDetailActivity.getIntent(requireContext()).apply {
                putExtra(EXTRA_SOURCE, SOURCE_OTHER_USER_ACTIVITY)
                putExtra(USER_ID_KEY, userId)
            }
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_SOURCE = "source"
        const val SOURCE_OTHER_USER_ACTIVITY = "otherUserActivity"
        const val USER_ID_KEY = "userId"

        fun newInstance(userId: Long): OtherUserActivityFragment {
            val fragment = OtherUserActivityFragment()
            val bundle = Bundle().apply {
                putLong(USER_ID_KEY, userId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

