package com.into.websoso.core.common.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Collects values from this [Flow] in a lifecycle-aware manner and invokes [onEvent] each time a value is emitted.
 *
 * This is intended for one-time events such as navigation, showing a Snackbar, or triggering a side-effect
 * that should not be preserved in a recomposition like [State] does.
 *
 * Collection starts when the provided [lifecycle] is at least in the [minActiveState], and stops
 * when the lifecycle falls below it. This avoids collecting the flow when the composable is not in
 * an active state (e.g., not visible).
 *
 * Unlike [collectAsStateWithLifecycle], this function does not retain the last value or cause recomposition.
 * It's designed for handling transient events such as user actions, notifications, or analytics tracking.
 *
 * Warning: [Lifecycle.State.INITIALIZED] is not allowed in this API. Passing it as a
 * parameter will throw an [IllegalArgumentException].
 *
 * Example usage:
 * ```
 * myViewModel.someEventFlow.collectAsEventWithLifecycle { event ->
 *     // Handle the event (e.g., show Snackbar or navigate)
 * }
 * ```
 *
 * @param onEvent Lambda that is invoked every time a new value is emitted from the flow.
 * @param lifecycle [Lifecycle] used to restart collecting this flow.
 * @param minActiveState [Lifecycle.State] in which the upstream flow gets collected. The collection will stop if the lifecycle falls below that state, and will restart if it's in that state again.
 * @param context [CoroutineContext] to use for collecting.
 */
@Composable
fun <T> Flow<T>.collectAsEventWithLifecycle(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    onEvent: suspend (T) -> Unit,
) {
    LaunchedEffect(this, lifecycle, minActiveState, context) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                this@collectAsEventWithLifecycle.collect { onEvent(it) }
            } else {
                withContext(context) {
                    this@collectAsEventWithLifecycle.collect { onEvent(it) }
                }
            }
        }
    }
}
