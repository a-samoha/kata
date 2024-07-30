package com.artsam.kata.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class SimpleRepository(
    private val serverApi: ServerExample = ServerExample
) {
    private val myCache =
        MutableSharedFlow<Int>(replay = 1, extraBufferCapacity = 1, BufferOverflow.DROP_OLDEST)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(fetchFromBE: Boolean = false) =
        if (fetchFromBE || myCache.replayCache.isEmpty())
            fetch()
                .flatMapLatest { myCache.asSharedFlow() }
                .catch { emitAll(myCache.asSharedFlow()) }
        else myCache.asSharedFlow()

    fun get(fetchFromBE: Boolean = false) =
        if (fetchFromBE || myCache.replayCache.isEmpty()) fetch()
        else myCache.asSharedFlow().take(1)

    suspend fun update(newValue: Int) = runCatching {
        ServerExample.update(newValue)
        myCache.emit(ServerExample.read())
    }

    suspend fun reset() = update(0)

    private fun fetch() = flow {
        emit(ServerExample.read())
    }.onEach { myCache.emit(it) }
        .catch { emit(SERVER_ERROR_VALUE) }

    companion object {
        const val SERVER_ERROR_VALUE = -1
    }
}

object ServerExample {
    private var counter = 0
    private var serverJob: Job? = null

    init {
        restartCounter()
    }

    private fun restartCounter() {
        serverJob?.cancel()
        serverJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(4000)
                counter++
                println("test counter $counter")
            }
        }
    }

    fun read() = counter

    suspend fun update(value: Int) {
        delay(300)
        counter = value
        restartCounter()
    }
}
