package com.into.websoso.ui.mapper

import com.into.websoso.data.model.CommentEntity
import com.into.websoso.data.model.FeedEntity
import com.into.websoso.domain.model.Feed
import com.into.websoso.ui.feedDetail.model.CommentModel
import com.into.websoso.ui.main.feed.model.FeedModel
import com.into.websoso.ui.main.feed.model.FeedModel.NovelModel
import com.into.websoso.ui.main.feed.model.FeedModel.UserModel

fun Feed.toUi(): FeedModel = FeedModel(
    user = UserModel(
        id = user.id,
        nickname = user.nickname,
        avatarImage = user.avatarImage,
    ),
    createdDate = createdDate,
    id = id,
    content = content,
    relevantCategories = relevantCategories,
    likeCount = likeCount,
    commentCount = commentCount,
    isModified = isModified,
    isSpoiler = isSpoiler,
    isLiked = isLiked,
    isMyFeed = isMyFeed,
    isPublic = isPublic,
    novel = NovelModel(
        id = novel.id,
        title = novel.title,
        rating = novel.rating,
        ratingCount = novel.ratingCount,
    ),
)

fun FeedEntity.toUi(): FeedModel = FeedModel(
    user = UserModel(
        id = user.id,
        nickname = user.nickname,
        avatarImage = user.avatarImage,
    ),
    createdDate = createdDate,
    id = id,
    content = content,
    relevantCategories = relevantCategories,
    likeCount = likeCount,
    commentCount = commentCount,
    isModified = isModified,
    isSpoiler = isSpoiler,
    isLiked = isLiked,
    isMyFeed = isMyFeed,
    isPublic = isPublic,
    novel = NovelModel(
        id = novel.id,
        title = novel.title,
        rating = novel.rating,
        ratingCount = novel.ratingCount,
    ),
)

fun CommentEntity.toUi(): CommentModel = CommentModel(
    user = UserModel(
        id = user.id,
        nickname = user.nickname,
        avatarImage = user.avatarImage,
    ),
    commentContent = commentContent,
    commentId = commentId,
    createdDate = createdDate,
    isModified = isModified,
    isMyComment = isMyComment,
    isHidden = isHidden,
    isSpoiler = isSpoiler,
    isBlocked = isBlocked,
)
