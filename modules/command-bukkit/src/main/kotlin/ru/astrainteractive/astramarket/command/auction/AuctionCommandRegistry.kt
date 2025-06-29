package ru.astrainteractive.astramarket.command.auction

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

@Suppress("LongParameterList")
internal class AuctionCommandRegistry(
    private val plugin: LifecyclePlugin,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val pluginTranslationKrate: CachedKrate<PluginTranslation>,
    private val router: GuiRouter,
    private val dispatchers: KotlinDispatchers,
    private val scope: CoroutineScope,
    private val itemStackEncoder: ItemStackEncoder,
    private val createAuctionUseCase: CreateAuctionUseCase

) {
    fun register() {
        plugin.setCommandExecutor(
            alias = "amarket",
            commandParser = AuctionCommandParser(),
            commandExecutor = AuctionCommandExecutor(
                router = router,
                dispatchers = dispatchers,
                scope = scope,
                itemStackEncoder = itemStackEncoder,
                createAuctionUseCase = createAuctionUseCase
            ),
            errorHandler = DefaultErrorHandler(
                kyoriKrate = kyoriKrate,
                pluginTranslationKrate = pluginTranslationKrate
            )
        )
    }
}
