package com.into.websoso.core.common.dispatchers

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
internal object DispatchersModule {
    @Provides
    @Dispatcher(WebsosoDispatchers.IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(
    val dispatchers: WebsosoDispatchers,
)

enum class WebsosoDispatchers {
    IO,
}
