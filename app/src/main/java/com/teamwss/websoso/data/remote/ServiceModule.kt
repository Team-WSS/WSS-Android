package com.teamwss.websoso.data.remote


import com.teamwss.websoso.data.NetworkModule
import com.teamwss.websoso.data.remote.api.FeedApi

object ServiceModule {
    val feedApi by lazy { NetworkModule.create<FeedApi>() }
}
