import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.encoding.IO
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.astramarket.di.DataModule
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
        DataModule.Default(
            dispatchers = DefaultKotlinDispatchers,
            dbSyntax = DBSyntax.SQLite,
            dbConnection = DBConnection.SQLite("db")
        )
    }
    private var module: DataModule? = null
    private val auctionsApi: AuctionsAPI
        get() = module?.auctionApi ?: error("Module is null")

    private val randomAuction: ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
        get() = ru.astrainteractive.astramarket.api.market.dto.AuctionDTO(
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
        val module = moduleFactory.invoke()
        module.database.openConnection()
        AuctionTable.create(module.database)
        this@AuctionsTests.module = module
    }

    @AfterTest
    fun destroy(): Unit = runBlocking {
        module?.database?.closeConnection()
        (module?.database?.dbConnection as? DBConnection.SQLite)?.let {
            File(it.dbName).delete()
        }
        module = null
    }

    @Test
    fun `Insert, fetch, expire same auction`(): Unit = runBlocking {
        val auction = randomAuction
        // Insert and fetch
        val id = auctionsApi.insertAuction(auction)!!
        var auctionDTO = auctionsApi.fetchAuction(id)!!
        assertEquals(auctionDTO.minecraftUuid, auction.minecraftUuid)
        // Get unexpiredAuctions
        var amount = auctionsApi.getUserAuctions(auctionDTO.minecraftUuid, false)!!.size
        assertEquals(amount, 1)
        // Expire
        auctionsApi.expireAuction(auctionDTO)
        assertEquals(auctionDTO.expired, false)
        auctionDTO = auctionsApi.fetchAuction(id)!!
        assertEquals(auctionDTO.expired, true)
        // Get expiredAuctions
        amount = auctionsApi.getUserAuctions(auctionDTO.minecraftUuid, true)!!.size
        assertEquals(amount, 1)
        // Get unexpiredAuctions
        amount = auctionsApi.getUserAuctions(auctionDTO.minecraftUuid, false)!!.size
        assertEquals(amount, 0)
        // Count auctions
        amount = auctionsApi.countPlayerAuctions(auctionDTO.minecraftUuid)!!
        assertEquals(amount, 1)
        // Delete and count auction
        auctionsApi.deleteAuction(auctionDTO)
        amount = auctionsApi.countPlayerAuctions(auctionDTO.minecraftUuid)!!
        assertEquals(amount, 0)
        val oldAuctionDTO = randomAuction.copy(time = 0)
        auctionsApi.insertAuction(oldAuctionDTO)
        amount = auctionsApi.getAuctionsOlderThan(System.currentTimeMillis() - 1)!!.size
        assertEquals(amount, 1)
    }
}
