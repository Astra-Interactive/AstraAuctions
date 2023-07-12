package com.astrainteractive.astramarket.di.impl

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.command.di.CommandsModule
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.getValue

object CommandsModuleImpl : CommandsModule {
    private val rootModule by RootModuleImpl
    override val translation: Dependency<Translation> = rootModule.translation
    override val configuration: Dependency<AuctionConfig> = rootModule.configuration
    override val plugin: Dependency<AstraMarket> = rootModule.plugin
    override val scope: Dependency<AsyncComponent> = rootModule.scope
    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
}
