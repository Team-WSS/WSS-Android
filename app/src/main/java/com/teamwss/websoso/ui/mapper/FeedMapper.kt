package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.CommentEntity
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.ui.main.feed.model.FeedModel
import com.teamwss.websoso.ui.main.feed.model.FeedModel.NovelModel
import com.teamwss.websoso.ui.main.feed.model.FeedModel.UserModel
import com.teamwss.websoso.ui.feedDetail.model.CommentModel

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
)
