package com.teamwss.websoso.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.teamwss.websoso.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecureStorageModule {
    private const val DEBUG_APP_PREFERENCES_NAME = "websoso_debug"
    private const val APP_PREFERENCES_NAME = "websoso"

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): SharedPreferences {
        return if (BuildConfig.DEBUG) {
            context.getSharedPreferences(DEBUG_APP_PREFERENCES_NAME, Context.MODE_PRIVATE)
        } else {
            try {
                createEncryptedSharedPreferences(APP_PREFERENCES_NAME, context)
            } catch (e: GeneralSecurityException) {
                deleteMasterKeyEntry()
                deleteExistingPreferences(APP_PREFERENCES_NAME, context)
                createEncryptedSharedPreferences(APP_PREFERENCES_NAME, context)
            }
        }
    }

    private fun deleteExistingPreferences(fileName: String, context: Context) {
        context.deleteSharedPreferences(fileName)
    }

    private fun deleteMasterKeyEntry() {
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
            deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        }
    }

    private fun createEncryptedSharedPreferences(
        fileName: String,
        context: Context,
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }
}
