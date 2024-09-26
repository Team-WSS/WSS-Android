package com.teamwss.websoso.ui.main.myPage.myActivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentMyActivityBinding
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity
import com.teamwss.websoso.ui.main.myPage.myActivity.adapter.MyActivityAdapter
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivityFragment :
    BaseFragment<FragmentMyActivityBinding>(R.layout.fragment_my_activity) {
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val myActivityAdapter: MyActivityAdapter by lazy {
        MyActivityAdapter()
    }
    private var userProfile: UserProfileModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMyActivitiesAdapter()
        setUpObserve()
        onMyActivityDetailButtonClick()
        loadUserProfile()
    }

    private fun setUpMyActivitiesAdapter() {
        binding.rvMyActivity.adapter = myActivityAdapter
    }

    private fun setUpObserve() {
        myActivityViewModel.myActivity.observe(viewLifecycleOwner) { activities ->
            myActivityAdapter.submitList(activities)
        }
    }

    private fun loadUserProfile() {
        arguments?.let { bundle ->
            val avatarImage = bundle.getString("avatarImage", "")
            val nickname = bundle.getString("nickname", "")
            val userId = bundle.getLong("userId", 0L)

            userProfile = UserProfileModel(
                avatarImage = avatarImage,
                nickname = nickname,
                userId = userId,
            ).also {
                myActivityAdapter.setUserProfile(it)
            }
        }
    }

    fun setupUserProfile(userProfile: UserProfileModel) {
        this.userProfile = userProfile
        updateMyActivityes()
    }

    private fun updateMyActivityes() {
        userProfile?.let { myActivityAdapter.setUserProfile(it) }
    }

    private fun onMyActivityDetailButtonClick() {
        binding.btnMyActivityMore.setOnClickListener {
            val intent = ActivityDetailActivity.getIntent(requireContext())
            intent.putExtra(EXTRA_SOURCE, SOURCE_MY_ACTIVITY)
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_SOURCE = "source"
        const val SOURCE_MY_ACTIVITY = "myActivity"
    }
}
