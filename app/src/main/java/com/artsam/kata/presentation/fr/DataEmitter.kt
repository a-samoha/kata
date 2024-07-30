package com.artsam.kata.presentation.fr

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class DataEmitter<T> {

    private val _dataFlow =
        MutableSharedFlow<T>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val dataFlow: Flow<T> = _dataFlow.asSharedFlow()

    suspend fun emitData(value: T) = _dataFlow.emit(value)
}
