package com.into.websoso.core.common.util

import kotlinx.coroutines.flow.MutableStateFlow

class MutableSingleStateFlow<T>(
    private val _stateFlow: MutableStateFlow<Event<T>?> = MutableStateFlow(null),
) : SingleStateFlow<T> by _stateFlow {
    fun emit(value: T) {
        _stateFlow.value = Event(value)
    }

    fun clear() {
        _stateFlow.value = null
    }
}
