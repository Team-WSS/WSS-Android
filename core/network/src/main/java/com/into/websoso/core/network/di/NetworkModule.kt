package com.into.websoso.core.network.di

import com.into.websoso.core.network.BuildConfig
import com.into.websoso.core.network.authenticator.AuthorizationAuthenticator
import com.into.websoso.core.network.interceptor.AuthorizationInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    private const val BASE_URL = BuildConfig.BASE_URL
    private const val CONNECT_TIME_LIMIT = 60L
    private const val READ_TIME_LIMIT = 30L
    private const val WRITE_TIME_LIMIT = 15L
    private const val CONTENT_TYPE = "application/json"
    private val httpLoggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(
        json: Json,
        client: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(CONTENT_TYPE.toMediaType()))
            .build()

    @Provides
    @Singleton
    internal fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        authorizationAuthenticator: AuthorizationAuthenticator,
        authorizationInterceptor: AuthorizationInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authorizationInterceptor)
            .authenticator(authorizationAuthenticator)
            .connectTimeout(CONNECT_TIME_LIMIT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_LIMIT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_LIMIT, TimeUnit.SECONDS)
            .build()
}
