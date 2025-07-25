package ru.astrainteractive.astramarket.api.market

import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.io.File
import java.util.UUID
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MarketApiTest {

    private var module: ApiMarketModule? = null
    private val marketApi: MarketApi
        get() = module?.marketApi ?: error("Module is null")

    private val randomAuction: MarketSlot
        get() = MarketSlot(
            id = -1,
            minecraftUuid = UUID.randomUUID().toString(),
            time = System.currentTimeMillis(),
            item = EncodedObject.ByteArray(ByteArray(0)),
            price = Random.nextInt().toFloat(),
            expired = false,
            minecraftUsername = "romaroman"
        )

    @BeforeTest
    fun setup(): Unit = runBlocking {
        File("./test").deleteRecursively()
        val module = ApiMarketModule.Default(
            dispatchers = DefaultKotlinDispatchers,
            yamlStringFormat = YamlStringFormat(),
            dataFolder = File("./test").also { it.deleteOnExit() },
            scope = CoroutineFeature.Unconfined(),
        )
        module.lifecycle.onEnable()
        this@MarketApiTest.module = module
    }

    @AfterTest
    fun destroy(): Unit = runBlocking {
        File("./test").deleteRecursively()
        module?.lifecycle?.onDisable()
        module = null
    }

    @Test
    fun `Insert, fetch, expire same auction`(): Unit = runBlocking {
        val auction = randomAuction
        // Insert and fetch
        val id = marketApi.insertSlot(auction)!!
        var auctionDTO = marketApi.getSlot(id)!!
        assertEquals(auctionDTO.minecraftUuid, auction.minecraftUuid)
        // Get unexpiredAuctions
        var amount = marketApi.getUserSlots(auctionDTO.minecraftUuid, false)!!.size
        assertEquals(amount, 1)
        // Expire
        marketApi.expireSlot(auctionDTO)
        assertEquals(auctionDTO.expired, false)
        auctionDTO = marketApi.getSlot(id)!!
        assertEquals(auctionDTO.expired, true)
        // Get expiredAuctions
        amount = marketApi.getUserSlots(auctionDTO.minecraftUuid, true)!!.size
        assertEquals(amount, 1)
        // Get unexpiredAuctions
        amount = marketApi.getUserSlots(auctionDTO.minecraftUuid, false)!!.size
        assertEquals(amount, 0)
        // Count auctions
        amount = marketApi.countPlayerSlots(auctionDTO.minecraftUuid)!!
        assertEquals(amount, 1)
        // Delete and count auction
        marketApi.deleteSlot(auctionDTO)
        amount = marketApi.countPlayerSlots(auctionDTO.minecraftUuid)!!
        assertEquals(amount, 0)
        val oldAuctionDTO = randomAuction.copy(time = 0)
        marketApi.insertSlot(oldAuctionDTO)
        amount = marketApi.getSlotsOlderThan(System.currentTimeMillis() - 1)!!.size
        assertEquals(amount, 1)
    }
}
