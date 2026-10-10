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
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

fun Modifier.debouncedClickable(
    debounceTime: Long = 500L,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier =
    composed {
        var lastClickTime by remember { mutableLongStateOf(0L) }
        val interactionSource = remember { MutableInteractionSource() }

        this.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = {
                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastClickTime >= debounceTime) {
                    lastClickTime = currentTime
                    onClick()
                }
            },
        )
    }

/**
 * 리플 효과가 없고 디바운스(중복 클릭 방지)가 적용된 선택 Modifier
 *
 * [selected] 상태를 접근성 트리에 노출하므로 탭/라디오처럼 선택 상태가 있는 요소에 사용한다.
 *
 * @param selected 현재 선택 여부 (스크린리더/키보드에 전달)
 * @param debounceTime 클릭 간격 제한 시간 (기본 500ms)
 * @param role 접근성 역할 (기본 [Role.Tab])
 * @param onClick 선택 시 실행할 로직
 */
fun Modifier.debouncedSelectable(
    selected: Boolean,
    debounceTime: Long = 500L,
    enabled: Boolean = true,
    role: Role = Role.Tab,
    onClick: () -> Unit,
): Modifier =
    composed {
        var lastClickTime by remember { mutableLongStateOf(0L) }
        val interactionSource = remember { MutableInteractionSource() }

        this.selectable(
            selected = selected,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = role,
            onClick = {
                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastClickTime >= debounceTime) {
                    lastClickTime = currentTime
                    onClick()
                }
            },
        )
    }
