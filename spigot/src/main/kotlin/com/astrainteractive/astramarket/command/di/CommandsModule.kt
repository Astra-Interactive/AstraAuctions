package com.astrainteractive.astramarket.command.di

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers

interface CommandsModule : Module {
    val translation: Dependency<Translation>
    val configuration: Dependency<AuctionConfig>
    val plugin: Dependency<AstraMarket>
    val scope: Dependency<AsyncComponent>
    val dispatchers: Dependency<BukkitDispatchers>
}
