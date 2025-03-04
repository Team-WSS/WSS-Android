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
import com.into.websoso.data.model.TermsAgreementEntity
import com.into.websoso.data.model.UserFeedsEntity
import com.into.websoso.data.model.UserInfoDetailEntity
import com.into.websoso.data.model.UserInfoEntity
import com.into.websoso.data.model.UserNovelStatsEntity
import com.into.websoso.data.model.UserProfileStatusEntity
import com.into.websoso.data.model.UserStorageEntity
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.request.TermsAgreementRequestDto
import com.into.websoso.data.remote.request.UserInfoRequestDto
import com.into.websoso.data.remote.request.UserProfileEditRequestDto
import com.into.websoso.data.remote.request.UserProfileStatusRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository
    @Inject
    constructor(
        private val userApi: UserApi,
        private val userStorage: DataStore<Preferences>,
    ) {
        val isTermsAgreementChecked: Flow<Boolean> = userStorage.data
            .map { preferences -> preferences[TERMS_AGREEMENT_CHECKED_KEY] ?: false }

        suspend fun fetchUserInfo(): UserInfoEntity {
            val userInfo = userApi.getUserInfo().toData()
            saveUserInfo(userInfo.userId, userInfo.nickname, userInfo.gender)
            return userInfo
        }

        private suspend fun saveUserInfo(
            userId: Long,
            nickname: String,
            gender: String,
        ) {
            userStorage.edit { preferences ->
                preferences[USER_ID_KEY] = userId.toString()
                preferences[USER_NICKNAME_KEY] = nickname
                preferences[USER_GENDER_KEY] = gender
            }
        }

        suspend fun fetchUserInfoDetail(): UserInfoDetailEntity = userApi.getUserInfoDetail().toData()

        suspend fun fetchBlockedUsers(): BlockedUsersEntity = userApi.getBlockedUser().toData()

        suspend fun deleteBlockedUser(blockId: Long) {
            userApi.deleteBlockedUser(blockId)
        }

        suspend fun saveBlockUser(userId: Long) {
            userApi.postBlockUser(userId)
        }

        suspend fun fetchUserNovelStats(userId: Long): UserNovelStatsEntity = userApi.getUserNovelStats(userId).toData()

        suspend fun fetchUserProfileStatus(): UserProfileStatusEntity = userApi.getProfileStatus().toData()

        suspend fun saveUserProfileStatus(isProfilePublic: Boolean) {
            userApi.patchProfileStatus(UserProfileStatusRequestDto(isProfilePublic))
        }

        suspend fun saveUserInfoDetail(
            gender: String,
            birthYear: Int,
        ) {
            userApi.putUserInfo(UserInfoRequestDto(gender, birthYear))
        }

        suspend fun fetchMyProfile(): MyProfileEntity = userApi.getMyProfile().toData()

        suspend fun fetchGenrePreference(userId: Long): List<GenrePreferenceEntity> =
            userApi.getGenrePreference(userId).genrePreferences.map {
                it.toData()
            }

        suspend fun fetchNovelPreferences(userId: Long): NovelPreferenceEntity = userApi.getNovelPreferences(userId).toData()

        suspend fun fetchOtherUserProfile(userId: Long): OtherUserProfileEntity = userApi.getOtherUserProfile(userId).toData()

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

        suspend fun fetchUserId(): Long {
            val preferences = userStorage.data.first()
            return preferences[USER_ID_KEY]?.toLongOrNull() ?: DEFAULT_USER_ID
        }

        suspend fun fetchIsLogin(): Boolean = fetchUserId() != DEFAULT_USER_ID

        suspend fun fetchGender(): String {
            val preferences = userStorage.data.first()
            return preferences[USER_GENDER_KEY] ?: DEFAULT_USER_GENDER
        }

        suspend fun saveGender(gender: String) {
            userStorage.edit { preferences ->
                preferences[USER_GENDER_KEY] = gender
            }
        }

        suspend fun fetchNovelDetailFirstLaunched() = userStorage.data.first()[NOVEL_DETAIL_FIRST_LAUNCHED_KEY] ?: true

        suspend fun fetchNicknameValidity(nickname: String): Boolean = userApi.getNicknameValidity(nickname).isValid

        suspend fun fetchUserStorage(
            userId: Long,
            readStatus: String,
            lastUserNovelId: Long,
            size: Int,
            sortType: String,
        ): UserStorageEntity =
            userApi
                .getUserStorage(
                    userId = userId,
                    readStatus = readStatus,
                    lastUserNovelId = lastUserNovelId,
                    size = size,
                    sortType = sortType,
                ).toData()

        suspend fun fetchUserFeeds(
            userId: Long,
            lastFeedId: Long,
            size: Int,
        ): UserFeedsEntity = userApi.getUserFeeds(userId, lastFeedId, size).toData()

        suspend fun fetchMyActivities(
            lastFeedId: Long,
            size: Int,
        ): UserFeedsEntity {
            val myUserId = fetchUserId()
            return fetchUserFeeds(myUserId, lastFeedId, size)
        }

        suspend fun fetchUserDeviceIdentifier(): String {
            val preferences = userStorage.data.first()
            return preferences[USER_DEVICE_IDENTIFIER_KEY] ?: ""
        }

        suspend fun saveUserDeviceIdentifier(deviceIdentifier: String) {
            userStorage.edit { preferences ->
                preferences[USER_DEVICE_IDENTIFIER_KEY] = deviceIdentifier
            }
        }

        suspend fun saveTermsAgreements(
            serviceAgreed: Boolean,
            privacyAgreed: Boolean,
            marketingAgreed: Boolean,
        ) {
            userApi.patchTermsAgreement(
                TermsAgreementRequestDto(serviceAgreed, privacyAgreed, marketingAgreed),
            )
            saveTermsAgreementChecked(serviceAgreed, privacyAgreed)
        }

        suspend fun fetchTermsAgreements(): TermsAgreementEntity {
            val termsAgreement = userApi.getTermsAgreement().toData()
            saveTermsAgreementChecked(termsAgreement.serviceAgreed, termsAgreement.privacyAgreed)
            return termsAgreement
        }

        private suspend fun saveTermsAgreementChecked(
            serviceAgreed: Boolean,
            privacyAgreed: Boolean,
        ) {
            userStorage.edit { preferences ->
                preferences[TERMS_AGREEMENT_CHECKED_KEY] = serviceAgreed && privacyAgreed
            }
        }

        suspend fun removeTermsAgreementChecked() {
            userStorage.edit { preferences ->
                preferences.remove(TERMS_AGREEMENT_CHECKED_KEY)
            }
        }

        companion object {
            val NOVEL_DETAIL_FIRST_LAUNCHED_KEY = booleanPreferencesKey("NOVEL_DETAIL_FIRST_LAUNCHED")
            val TERMS_AGREEMENT_CHECKED_KEY = booleanPreferencesKey("terms_agreement_checked")
            val USER_ID_KEY = stringPreferencesKey("USER_ID")
            val USER_NICKNAME_KEY = stringPreferencesKey("USER_NICKNAME")
            val USER_GENDER_KEY = stringPreferencesKey("USER_GENDER")
            val USER_DEVICE_IDENTIFIER_KEY = stringPreferencesKey("USER_DEVICE_IDENTIFIER")
            const val DEFAULT_USER_ID = -1L
            const val DEFAULT_USER_GENDER = "F"
        }
    }
