package com.into.websoso.feed

import com.into.websoso.data.feed.repository.UpdatedFeedRepository
import com.into.websoso.feed.mapper.toDomain
import com.into.websoso.feed.mapper.toFeedEntity
import com.into.websoso.feed.model.Feed
import com.into.websoso.feed.model.Feeds
import com.into.websoso.user.UserRepository
import com.into.websoso.user.model.MyProfileEntity
import com.into.websoso.user.model.UserFeedsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMyFeedsUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val feedRepository: UpdatedFeedRepository,
    ) {
        val myFeedsFlow: Flow<List<Feed>> = feedRepository.myFeeds
            .map { list -> list.map { it.toDomain() } }

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

            val myFeedsEntity: UserFeedsEntity = userRepository.fetchMyActivities(
                lastFeedId = lastFeedId,
                size = if (isFeedRefreshed) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
                genres = genres?.toTypedArray(),
                isVisible = isVisible,
                isUnVisible = isUnVisible,
                sortCriteria = sortCriteria,
            )

            val convertedFeeds = myFeedsEntity.feeds.map { userFeed ->
                userFeed.toFeedEntity(userProfile = profile, userId = myId)
            }

            feedRepository.updateMyFeedsCache(
                feeds = convertedFeeds,
                isRefreshed = isFeedRefreshed,
            )

            return Feeds(
                category = "내 활동",
                isLoadable = myFeedsEntity.isLoadable,
                totalCount = myFeedsEntity.feedsCount,
                feeds = emptyList(),
            )
        }

        companion object {
            private const val INITIAL_ID: Long = 0
            private const val INITIAL_REQUEST_SIZE = 40
            private const val ADDITIONAL_REQUEST_SIZE = 20
        }
    }
