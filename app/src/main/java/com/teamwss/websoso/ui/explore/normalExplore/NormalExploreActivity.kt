package com.teamwss.websoso.ui.explore.normalExplore

import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNormalExploreBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class NormalExploreActivity :
    BindingActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_explore)
    }
}