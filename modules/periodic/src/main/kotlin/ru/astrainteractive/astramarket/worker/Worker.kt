package ru.astrainteractive.astramarket.worker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.withTimings
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import java.util.Timer
import kotlin.time.Duration

internal abstract class Worker(val key: String) {
    private var scheduler: Timer? = null
    private var scope: CoroutineScope? = null

    abstract val dispatcher: CoroutineDispatcher
    abstract val initialDelay: Duration
    abstract val period: Duration

    abstract suspend fun doWork()

    fun start() {
        if (scope != null) error("Scope already exists!")
        if (scheduler != null) error("Scheduler already exists!")
        val currentScope = CoroutineFeature.IO.withTimings()
        scope = currentScope
        scheduler = kotlin.concurrent.timer(
            name = key,
            daemon = false,
            initialDelay = initialDelay.inWholeMilliseconds,
            period = period.inWholeMilliseconds,
            action = { currentScope.launch(dispatcher) { doWork() } }
        )
    }

    fun stop() {
        scope?.cancel()
        scope = null
        scheduler?.cancel()
        scheduler = null
    }
}
