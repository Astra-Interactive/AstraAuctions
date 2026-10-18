package ru.astrainteractive.astramarket.market.bukkit

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.core.util.toItemStack
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.api.AuctionsSorter
import ru.astrainteractive.astramarket.market.model.AuctionSort
import ru.astrainteractive.klibs.mikro.core.util.sortedBy
import java.util.UUID

internal class BukkitAuctionsSorter(private val objectEncoder: ObjectEncoder) : AuctionsSorter {
    override fun sort(slots: List<MarketSlot>, sort: AuctionSort): List<MarketSlot> = when (sort) {
        is AuctionSort.Material -> slots.sortedBy(sort.isAsc) { slot ->
            objectEncoder.toItemStack(slot.item).getOrNull()?.type
        }

        is AuctionSort.Date -> slots.sortedBy(sort.isAsc) { slot -> slot.time }

        is AuctionSort.Name -> slots.sortedBy(sort.isAsc) { slot ->
            objectEncoder.toItemStack(slot.item).getOrNull()?.itemMeta?.displayName
        }

        is AuctionSort.Price -> slots.sortedBy(sort.isAsc) { slot -> slot.price }

        is AuctionSort.Player -> slots.sortedBy(sort.isAsc) { slot ->
            Bukkit.getOfflinePlayer(UUID.fromString(slot.minecraftUuid)).name.orEmpty()
        }
    }
}
