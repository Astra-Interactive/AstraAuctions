package ru.astrainteractive.astramarket.market.fake

import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import java.util.UUID

class FakeAuctionsBridge(
    private val names: Map<UUID, String>
) : AuctionsBridge {
    var isInventoryFull = false
    var isItemValid = true
    var permissionLimit: Int? = null
    var hasExpirePermission = false
    val itemsGiven = mutableListOf<GivenItem>()

    override suspend fun isInventoryFull(uuid: UUID): Boolean = isInventoryFull

    override suspend fun addItemToInventory(marketSlot: MarketSlot, uuid: UUID) {
        itemsGiven += GivenItem(marketSlot, uuid)
    }

    override suspend fun itemDesc(marketSlot: MarketSlot): String = "Diamond"

    override suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int? = permissionLimit

    override fun playerName(uuid: UUID): String? = names[uuid]

    override fun hasExpirePermission(uuid: UUID): Boolean = hasExpirePermission

    override fun isItemValid(marketSlot: MarketSlot): Boolean = isItemValid

    data class GivenItem(val marketSlot: MarketSlot, val receiver: UUID)
}
