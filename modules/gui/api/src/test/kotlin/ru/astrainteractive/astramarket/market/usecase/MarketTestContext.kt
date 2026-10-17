package ru.astrainteractive.astramarket.market.usecase

import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.data.dao.market.fake.FakeMarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.fake.FakeAuctionsBridge
import ru.astrainteractive.astramarket.market.fake.FakePlayerInteractionBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.util.UUID
import kotlin.time.Instant

class MarketTestContext(
    config: PluginConfig,
    slots: List<MarketSlot>
) {
    val seller: UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    val buyer: UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002")
    val translation = PluginTranslation()
    val translationKrate: CachedKrate<PluginTranslation> = DefaultMutableKrate(
        factory = { translation },
        loader = { null }
    ).asCachedKrate()
    val configKrate: CachedKrate<PluginConfig> = DefaultMutableKrate(
        factory = { config },
        loader = { null }
    ).asCachedKrate()
    val marketRepository = FakeMarketRepository(slots)
    val auctionsBridge = FakeAuctionsBridge(names = mapOf(seller to "Seller", buyer to "Buyer"))
    val playerInteractionBridge = FakePlayerInteractionBridge()
    val config: PluginConfig = config

    companion object {
        fun slot(id: Int, owner: UUID, price: Float): MarketSlot {
            return MarketSlot(
                id = id,
                minecraftUuid = owner.toString(),
                minecraftUsername = "Seller",
                time = Instant.fromEpochMilliseconds(0),
                item = EncodedObject.ByteArray(byteArrayOf(1)),
                price = price,
                expired = false
            )
        }
    }
}
