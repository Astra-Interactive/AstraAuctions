package ru.astrainteractive.astramarket.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

object CoroutineExt {
    fun CoroutineScope.launchWithLock(
        mutex: Mutex,
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) = launch(context) {
        mutex.withLock {
            block.invoke(this)
        }
    }
}
