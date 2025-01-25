package com.into.websoso.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.data.remote.api.PushMessageApi
import com.into.websoso.data.remote.request.NotificationPushEnabledRequestDto
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PushMessageRepository
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository,
        private val userStorage: DataStore<Preferences>,
        private val pushMessageApi: PushMessageApi,
    ) {
        suspend fun updateUserFCMToken(fcmToken: String) {
            val storedToken = fetchUserFCMToken()
            if (fcmToken == storedToken) {
                return
            }

            saveUserFCMToken(fcmToken)
        }

        suspend fun saveUserFCMToken(fcmToken: String) {
            userStorage.edit { preferences ->
                preferences[USER_FCM_TOKEN_KEY] = fcmToken
            }

            saveUserFCMTokenToRemote(fcmToken)
        }

        private suspend fun saveUserFCMTokenToRemote(fcmToken: String) {
            val deviceIdentifier = userRepository.fetchUserDeviceIdentifier()
            authRepository.saveFCMToken(fcmToken, deviceIdentifier)
        }

        private suspend fun fetchUserFCMToken(): String {
            val preferences = userStorage.data.first()
            return preferences[USER_FCM_TOKEN_KEY] ?: ""
        }

        suspend fun saveNotificationPermissionFirstLaunched(value: Boolean) {
            userStorage.edit { preferences ->
                preferences[NOTIFICATION_PERMISSION_FIRST_LAUNCHED_KEY] = value
            }
        }

        suspend fun fetchNotificationPermissionFirstLaunched() =
            userStorage.data.first()[NOTIFICATION_PERMISSION_FIRST_LAUNCHED_KEY] ?: true

        suspend fun fetchUserPushEnabled(): Boolean = pushMessageApi.getUserPushEnabled().isPushEnabled

        suspend fun saveUserPushEnabled(isEnabled: Boolean) {
            pushMessageApi.postUserPushEnabled(
                notificationPushEnabledRequestDto = NotificationPushEnabledRequestDto(
                    isPushEnabled = isEnabled,
                ),
            )
        }

        companion object {
            val USER_FCM_TOKEN_KEY = stringPreferencesKey("USER_FCM_TOKEN")
            val NOTIFICATION_PERMISSION_FIRST_LAUNCHED_KEY =
                booleanPreferencesKey("NOTIFICATION_PERMISSION_FIRST_LAUNCHED")
        }
    }
