package com.teamwss.websoso.data.remote

import com.teamwss.websoso.data.di.NetworkModule
import com.teamwss.websoso.data.remote.api.FeedApi

object ServiceModule {
    val feedApi = NetworkModule.provideService<FeedApi>(
        NetworkModule.provideRetrofit(
            NetworkModule.provideLogOkHttpClient()
        )
    )
}
