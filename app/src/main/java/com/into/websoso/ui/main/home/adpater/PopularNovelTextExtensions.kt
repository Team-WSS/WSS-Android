package com.into.websoso.ui.main.home.adpater

import android.content.Context
import android.text.TextPaint
import android.widget.TextView
import com.into.websoso.core.resource.R.string.home_popular_novel_author_status
import com.into.websoso.core.resource.R.string.home_popular_novel_status_completed
import com.into.websoso.core.resource.R.string.home_popular_novel_status_serial
import com.into.websoso.core.resource.R.string.home_popular_novel_title_ellipsis
import com.into.websoso.data.model.PopularNovelsEntity.PopularNovelEntity

internal fun PopularNovelEntity.toPopularNovelTitle(titleView: TextView): String =
    title
        .takeWithEllipsis(
            maxLength = POPULAR_NOVEL_TITLE_VISIBLE_LENGTH,
            ellipsis = titleView.context.getString(home_popular_novel_title_ellipsis),
        ).wrapByWord(
            textPaint = titleView.paint,
            maxWidth = titleView.textAreaWidth,
        )

internal fun PopularNovelEntity.toAuthorStatus(context: Context): String {
    val authorName = author.take(MAX_AUTHOR_LENGTH)
    val status =
        context.getString(
            if (isNovelCompleted) {
                home_popular_novel_status_completed
            } else {
                home_popular_novel_status_serial
            },
        )

    return if (authorName.isBlank()) {
        status
    } else {
        context.getString(home_popular_novel_author_status, authorName, status)
    }
}

private val TextView.textAreaWidth: Float
    get() {
        val measuredWidth = width.takeIf { it > 0 } ?: layoutParams.width
        return (measuredWidth - paddingStart - paddingEnd).toFloat()
    }

private fun String.takeWithEllipsis(
    maxLength: Int,
    ellipsis: String,
): String =
    if (length > maxLength) {
        take(maxLength) + ellipsis
    } else {
        this
    }

private fun String.wrapByWord(
    textPaint: TextPaint,
    maxWidth: Float,
): String {
    val words = trim().split(WORD_SEPARATOR_REGEX)
    if (words.size < MIN_WORD_COUNT_FOR_WRAP || textPaint.measureText(this) <= maxWidth) return this

    var firstLine = words.first()
    words.drop(1).forEachIndexed { index, word ->
        val nextLine = "$firstLine $word"
        if (textPaint.measureText(nextLine) > maxWidth) {
            return firstLine + LINE_BREAK + words.drop(index + 1).joinToString(" ")
        }
        firstLine = nextLine
    }

    return this
}

private val WORD_SEPARATOR_REGEX = Regex("\\s+")

private const val POPULAR_NOVEL_TITLE_VISIBLE_LENGTH = 17
private const val MIN_WORD_COUNT_FOR_WRAP = 2
private const val LINE_BREAK = "\n"
private const val MAX_AUTHOR_LENGTH = 6
