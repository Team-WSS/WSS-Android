package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.domain.model.Profile
import javax.inject.Inject

class SaveChangedProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(newProfile: Profile, previousProfile: Profile) {
        val changedProfile = compareProfile(newProfile, previousProfile)
        userRepository.saveUserProfile(
            avatarId = changedProfile.avatarId,
            nickname = changedProfile.nickname,
            intro = changedProfile.introduction,
            genrePreferences = changedProfile.genrePreferences,
        )
    }

    private fun compareProfile(newProfile: Profile, previousProfile: Profile): Profile {
        val avatarId = (previousProfile.avatarId to newProfile.avatarId).compareAndReturnNewOrNullValue()
        val nickname = (previousProfile.nickname to newProfile.nickname).compareAndReturnNewOrNullValue()
        val introduction = (previousProfile.introduction to newProfile.introduction).compareAndReturnNewOrNullValue()
        val genrePreferences = (previousProfile.genrePreferences to newProfile.genrePreferences).compareAndReturnNewOrNullValue()

        return Profile(
            avatarId = avatarId ?: previousProfile.avatarId,
            nickname = nickname ?: previousProfile.nickname,
            introduction = introduction ?: previousProfile.introduction,
            genrePreferences = genrePreferences ?: previousProfile.genrePreferences,
        )
    }

    private fun <T> Pair<T, T>.compareAndReturnNewOrNullValue(): T? {
        val (oldValue, newValue) = this
        return if (oldValue == newValue) null else newValue
    }
}
