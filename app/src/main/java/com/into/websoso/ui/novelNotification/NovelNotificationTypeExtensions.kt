package com.into.websoso.ui.novelNotification

import androidx.annotation.StringRes
import com.into.websoso.core.resource.R.string.novel_notification_completion_title
import com.into.websoso.core.resource.R.string.novel_notification_hiatus_return_title
import com.into.websoso.domain.model.NovelNotificationType
import com.into.websoso.domain.model.NovelNotificationType.COMPLETION
import com.into.websoso.domain.model.NovelNotificationType.HIATUS_RETURN

@StringRes
fun NovelNotificationType.novelNotificationTitleRes(): Int =
    when (this) {
        COMPLETION -> novel_notification_completion_title
        HIATUS_RETURN -> novel_notification_hiatus_return_title
    }
