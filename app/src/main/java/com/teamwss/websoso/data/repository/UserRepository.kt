package com.teamwss.websoso.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.data.model.OtherUserProfileEntity
import com.teamwss.websoso.data.model.UserInfoEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.remote.api.UserApi
import com.teamwss.websoso.data.remote.request.UserInfoRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileEditRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileStatusRequestDto
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val userStorage: DataStore<Preferences>,
) {

    suspend fun fetchUserInfo(): UserInfoEntity {
        return userApi.getUserInfo().toData()
    }

    suspend fun fetchBlockedUsers(): BlockedUsersEntity {
        return userApi.getBlockedUser().toData()
    }

    suspend fun deleteBlockedUser(blockId: Long) {
        userApi.deleteBlockedUser(blockId)
    }

    suspend fun fetchUserNovelStats(): UserNovelStatsEntity {
        return userApi.getUserNovelStats().toData()
    }

    suspend fun fetchUserProfileStatus(): UserProfileStatusEntity {
        return userApi.getProfileStatus().toData()
    }

    suspend fun saveUserProfileStatus(isProfilePublic: Boolean) {
        userApi.patchProfileStatus(UserProfileStatusRequestDto(isProfilePublic))
    }

    suspend fun saveUserInfo(gender: String, birthYear: Int) {
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

    suspend fun saveUserProfile(
        nickname: String,
        gender: String,
        birth: Int,
        genrePreferences: List<String>,
    ) {
        userApi.postUserProfile(
            UserProfileRequestDto(
                nickname,
                gender,
                birth,
                genrePreferences,
            )
        )
    }

    suspend fun fetchNicknameValidity(nickname: String): Boolean {
        return userApi.getNicknameValidity(nickname).isValid
    }

    suspend fun saveUserProfile(avatarId: Int?, nickname: String?, intro: String?, genrePreferences: List<String>) {
        userApi.patchProfile(UserProfileEditRequestDto(avatarId, nickname, intro, genrePreferences))
    }

    suspend fun saveNovelDetailFirstLaunched(value: Boolean) {
        userStorage.edit { preferences ->
            preferences[NOVEL_DETAIL_FIRST_LAUNCHED_KEY] = value
        }
    }

    suspend fun fetchNovelDetailFirstLaunched(): Boolean {
        return userStorage.data.first()[NOVEL_DETAIL_FIRST_LAUNCHED_KEY] ?: true
    }

    suspend fun saveAccessToken(value: String) {
        userStorage.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = value
        }
    }

    suspend fun fetchAccessToken(): String {
        return userStorage.data.first()[ACCESS_TOKEN_KEY].orEmpty()
    }

    suspend fun saveRefreshToken(value: String) {
        userStorage.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = value
        }
    }

    suspend fun fetchRefreshToken(): String {
        return userStorage.data.first()[REFRESH_TOKEN_KEY].orEmpty()
    }

    suspend fun clearTokens() {
        userStorage.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    companion object {
        val NOVEL_DETAIL_FIRST_LAUNCHED_KEY = booleanPreferencesKey("NOVEL_DETAIL_FIRST_LAUNCHED")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("ACCESS_TOKEN")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("REFRESH_TOKEN")
    }
}
