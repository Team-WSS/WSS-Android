package com.into.websoso.core.common.util

import kotlinx.coroutines.flow.StateFlow

typealias SingleStateFlow<T> = StateFlow<Event<T>?>
