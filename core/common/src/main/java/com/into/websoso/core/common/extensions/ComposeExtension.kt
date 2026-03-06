package com.into.websoso.core.common.extensions

/**
 * 리플 효과가 없고 디바운스(중복 클릭 방지)가 적용된 클릭 Modifier
 *
 * @param debounceTime 클릭 간격 제한 시간 (기본 500ms)
 * @param onClick 클릭 시 실행할 로직
 */
import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.debouncedClickable(
    debounceTime: Long = 500L,
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val interactionSource = remember { MutableInteractionSource() }

    this.clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime >= debounceTime) {
                lastClickTime = currentTime
                onClick()
            }
        },
    )
}
