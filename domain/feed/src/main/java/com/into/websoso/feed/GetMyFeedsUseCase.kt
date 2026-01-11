package com.into.websoso.feed

import com.into.websoso.feed.mapper.toDomain
import com.into.websoso.feed.model.Feeds
import com.into.websoso.user.UserRepository
import com.into.websoso.user.model.MyProfileEntity
import com.into.websoso.user.model.UserFeedsEntity
import javax.inject.Inject

class GetMyFeedsUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        private var myProfile: MyProfileEntity? = null
        private var myId: Long? = null

        suspend operator fun invoke(
            lastFeedId: Long = INITIAL_ID,
            size: Int = INITIAL_REQUEST_SIZE,
            genres: List<String>? = null,
            isVisible: Boolean? = null,
            isUnVisible: Boolean? = null,
            sortCriteria: String? = null,
        ): Feeds {
            val isFeedRefreshed: Boolean = lastFeedId == INITIAL_ID
            val profile = myProfile ?: userRepository.fetchMyProfile().also { myProfile = it }
            val myId = myId ?: userRepository.fetchUserInfo().userId.also { myId = it }

            val myFeeds: UserFeedsEntity = userRepository.fetchMyActivities(
                lastFeedId = lastFeedId,
                size = if (isFeedRefreshed) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
                genres = genres?.toTypedArray(),
                isVisible = isVisible,
                isUnVisible = isUnVisible,
                sortCriteria = sortCriteria,
            )

            return Feeds(
                category = "내 활동",
                isLoadable = myFeeds.isLoadable,
                feeds = myFeeds.feeds.map { it.toDomain(myProfile = profile, id = myId) },
            )
        }

        companion object {
            private const val INITIAL_ID: Long = 0
            private const val INITIAL_REQUEST_SIZE = 40
            private const val ADDITIONAL_REQUEST_SIZE = 20
        }
    }
