import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.encoding.IO
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.io.File
import java.util.UUID
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuctionsTests {
    private val moduleFactory = {
        ApiMarketModule.Default(
            dispatchers = DefaultKotlinDispatchers,
            dbSyntax = DBSyntax.SQLite,
            dbConnection = DBConnection.SQLite("db.db")
        )
    }
    private var module: ApiMarketModule? = null
    private val marketApi: MarketApi
        get() = module?.marketApi ?: error("Module is null")

    private val randomAuction: ru.astrainteractive.astramarket.api.market.dto.MarketSlot
        get() = ru.astrainteractive.astramarket.api.market.dto.MarketSlot(
            id = -1,
            discordId = UUID.randomUUID().toString(),
            minecraftUuid = UUID.randomUUID().toString(),
            time = System.currentTimeMillis(),
            item = IO.ByteArray(ByteArray(0)),
            price = Random.nextInt().toFloat(),
            expired = false
        )

    @BeforeTest
    fun setup(): Unit = runBlocking {
        File("db.db").delete()
        val module = moduleFactory.invoke()
        module.database.openConnection()
        AuctionTable.create(module.database)
        this@AuctionsTests.module = module
    }

    @AfterTest
    fun destroy(): Unit = runBlocking {
        module?.database?.closeConnection()
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
