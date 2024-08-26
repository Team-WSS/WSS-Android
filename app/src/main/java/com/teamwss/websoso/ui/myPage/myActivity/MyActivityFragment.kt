package com.teamwss.websoso.ui.myPage.myActivity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentMyActivityBinding
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.ui.myPage.myActivity.adapter.MyActivityAdapter
import com.teamwss.websoso.ui.myPage.myLibrary.adapter.RestGenrePreferenceAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivityFragment :
    BaseFragment<FragmentMyActivityBinding>(R.layout.fragment_my_activity) {
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val myActivityAdapter: MyActivityAdapter by lazy {
        MyActivityAdapter(myActivityViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMyActivitiesAdapter()
        setUpObserve()
    }

    fun setupMyActivitiesAdapter(){
        binding.rvMyActivity.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myActivityAdapter
        }
    }

    private fun setUpObserve() {
        myActivityViewModel.myActivity.observe(viewLifecycleOwner) { activities ->
            Log.d("MyActivityFragment", "Observed activities: $activities")
            myActivityAdapter.submitList(activities)
        }
    }
}