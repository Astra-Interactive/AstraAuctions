package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astratemplate.AstraMarket
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.di.module

val BStatsModule = module {
    Metrics(AstraMarket.instance, 15771)
}