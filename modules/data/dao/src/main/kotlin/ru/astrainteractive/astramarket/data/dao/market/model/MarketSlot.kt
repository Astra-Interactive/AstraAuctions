@file:Suppress("BareIdentifier", "DataClassContainsFunctions")

package ru.astrainteractive.astramarket.data.dao.market.model

import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Instant

data class MarketSlot(
    val id: Int,
    val minecraftUuid: String,
    val minecraftUsername: String,
    val time: Instant,
    val item: EncodedObject.ByteArray,
    val price: Float,
    val expired: Boolean,
) {
    fun isOwnedBy(uuid: UUID): Boolean = minecraftUuid == uuid.toString()

    fun isLifetimeOver(now: Instant, maxLifetime: Duration): Boolean = now - time > maxLifetime
}
