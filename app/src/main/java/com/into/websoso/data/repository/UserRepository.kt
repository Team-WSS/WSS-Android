package com.into.websoso.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.BlockedUsersEntity
import com.into.websoso.data.model.GenrePreferenceEntity
import com.into.websoso.data.model.MyProfileEntity
import com.into.websoso.data.model.NovelPreferenceEntity
import com.into.websoso.data.model.OtherUserProfileEntity
import com.into.websoso.data.model.UserFeedsEntity
import com.into.websoso.data.model.UserInfoDetailEntity
import com.into.websoso.data.model.UserInfoEntity
import com.into.websoso.data.model.UserNovelStatsEntity
import com.into.websoso.data.model.UserProfileStatusEntity
import com.into.websoso.data.model.UserStorageEntity
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.request.UserInfoRequestDto
import com.into.websoso.data.remote.request.UserProfileEditRequestDto
import com.into.websoso.data.remote.request.UserProfileStatusRequestDto
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val userStorage: DataStore<Preferences>,
) {

    suspend fun fetchUserInfo(): UserInfoEntity {
        val userInfo = userApi.getUserInfo().toData()
        saveUserInfo(userInfo.userId, userInfo.nickname, userInfo.gender)
        return userInfo
    }

    private suspend fun saveUserInfo(userId: Long, nickname: String, gender: String) {
        userStorage.edit { preferences ->
            preferences[USER_ID_KEY] = userId.toString()
            preferences[USER_NICKNAME_KEY] = nickname
            preferences[USER_GENDER_KEY] = gender
        }
    }

    suspend fun fetchUserInfoDetail(): UserInfoDetailEntity {
        return userApi.getUserInfoDetail().toData()
    }

    suspend fun fetchBlockedUsers(): BlockedUsersEntity {
        return userApi.getBlockedUser().toData()
    }

    suspend fun deleteBlockedUser(blockId: Long) {
        userApi.deleteBlockedUser(blockId)
    }

    suspend fun saveBlockUser(userId: Long) {
        userApi.postBlockUser(userId)
    }

    suspend fun fetchUserNovelStats(userId: Long): UserNovelStatsEntity {
        return userApi.getUserNovelStats(userId).toData()
    }

    suspend fun fetchUserProfileStatus(): UserProfileStatusEntity {
        return userApi.getProfileStatus().toData()
    }

    suspend fun saveUserProfileStatus(isProfilePublic: Boolean) {
        userApi.patchProfileStatus(UserProfileStatusRequestDto(isProfilePublic))
    }

    suspend fun saveUserInfoDetail(gender: String, birthYear: Int) {
        userApi.putUserInfo(UserInfoRequestDto(gender, birthYear))
    }

    suspend fun fetchMyProfile(): MyProfileEntity {
        return userApi.getMyProfile().toData()
    }

    suspend fun fetchGenrePreference(userId: Long): List<GenrePreferenceEntity> {
        return userApi.getGenrePreference(userId).genrePreferences.map { it.toData() }
    }

    suspend fun fetchNovelPreferences(userId: Long): NovelPreferenceEntity {
        return userApi.getNovelPreferences(userId).toData()
    }

    suspend fun fetchOtherUserProfile(userId: Long): OtherUserProfileEntity {
        return userApi.getOtherUserProfile(userId).toData()
    }

    suspend fun saveEditingUserProfile(
        avatarId: Int?,
        nickname: String?,
        intro: String?,
        genrePreferences: List<String>,
    ) {
        userApi.patchProfile(UserProfileEditRequestDto(avatarId, nickname, intro, genrePreferences))
    }

    suspend fun saveNovelDetailFirstLaunched(value: Boolean) {
        userStorage.edit { preferences ->
            preferences[NOVEL_DETAIL_FIRST_LAUNCHED_KEY] = value
        }
    }

    suspend fun saveNotificationPermissionFirstLaunched(value: Boolean) {
        userStorage.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_FIRST_LAUNCHED_KEY] = value
        }
    }

    suspend fun fetchNotificationPermissionFirstLaunched(): Boolean {
        return userStorage.data.first()[NOTIFICATION_PERMISSION_FIRST_LAUNCHED_KEY] ?: true
    }

    suspend fun fetchUserId(): Long {
        val preferences = userStorage.data.first()
        return preferences[USER_ID_KEY]?.toLongOrNull() ?: DEFAULT_USER_ID
    }

    suspend fun fetchIsLogin(): Boolean {
        return fetchUserId() != DEFAULT_USER_ID
    }

    suspend fun fetchGender(): String {
        val preferences = userStorage.data.first()
        return preferences[USER_GENDER_KEY] ?: DEFAULT_USER_GENDER
    }

    suspend fun saveGender(gender: String) {
        userStorage.edit { preferences ->
            preferences[USER_GENDER_KEY] = gender
        }
    }

    suspend fun fetchNovelDetailFirstLaunched() =
        userStorage.data.first()[NOVEL_DETAIL_FIRST_LAUNCHED_KEY] ?: true

    suspend fun fetchNicknameValidity(nickname: String): Boolean {
        return userApi.getNicknameValidity(nickname).isValid
    }

    suspend fun fetchUserStorage(
        userId: Long,
        readStatus: String,
        lastUserNovelId: Long,
        size: Int,
        sortType: String,
    ): UserStorageEntity {
        return userApi.getUserStorage(
            userId = userId,
            readStatus = readStatus,
            lastUserNovelId = lastUserNovelId,
            size = size,
            sortType = sortType,
        ).toData()
    }

    suspend fun fetchUserFeeds(userId: Long, lastFeedId: Long, size: Int): UserFeedsEntity {
        return userApi.getUserFeeds(userId, lastFeedId, size).toData()
    }

    suspend fun fetchMyActivities(lastFeedId: Long, size: Int): UserFeedsEntity {
        val myUserId = fetchUserId()
        return fetchUserFeeds(myUserId, lastFeedId, size)
    }

    companion object {
        val NOVEL_DETAIL_FIRST_LAUNCHED_KEY = booleanPreferencesKey("NOVEL_DETAIL_FIRST_LAUNCHED")
        val NOTIFICATION_PERMISSION_FIRST_LAUNCHED_KEY =
            booleanPreferencesKey("NOTIFICATION_PERMISSION_FIRST_LAUNCHED")
        val USER_ID_KEY = stringPreferencesKey("USER_ID")
        val USER_NICKNAME_KEY = stringPreferencesKey("USER_NICKNAME")
        val USER_GENDER_KEY = stringPreferencesKey("USER_GENDER")
        const val DEFAULT_USER_ID = -1L
        const val DEFAULT_USER_GENDER = "F"
    }
}
