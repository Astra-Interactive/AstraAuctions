import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.encoding.IO
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.api.market.impl.AuctionsAPIImpl
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapperImpl
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.util.UUID
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuctionsTests : ORMTest(builder = { DefaultDatabase(DBConnection.SQLite("db.db"), DBSyntax.SQLite) }) {
    private val dataSource: ru.astrainteractive.astramarket.api.market.AuctionsAPI
        get() = ru.astrainteractive.astramarket.api.market.impl.AuctionsAPIImpl(
            database = assertConnected(),
            auctionMapper = AuctionMapperImpl(),
            dispatchers = DefaultKotlinDispatchers
        )
    val randomAuction: ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
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
    override fun setup(): Unit = runBlocking {
        super.setup()
        val database = assertConnected()
        AuctionTable.create(database)
    }

    @Test
    fun `Insert, fetch, expire same auction`(): Unit = runBlocking {
        val auction = randomAuction
        // Insert and fetch
        val id = dataSource.insertAuction(auction)!!
        var auctionDTO = dataSource.fetchAuction(id)!!
        assertEquals(auctionDTO.minecraftUuid, auction.minecraftUuid)
        // Get unexpiredAuctions
        var amount = dataSource.getUserAuctions(auctionDTO.minecraftUuid, false)!!.size
        assertEquals(amount, 1)
        // Expire
        dataSource.expireAuction(auctionDTO)
        assertEquals(auctionDTO.expired, false)
        auctionDTO = dataSource.fetchAuction(id)!!
        assertEquals(auctionDTO.expired, true)
        // Get expiredAuctions
        amount = dataSource.getUserAuctions(auctionDTO.minecraftUuid, true)!!.size
        assertEquals(amount, 1)
        // Get unexpiredAuctions
        amount = dataSource.getUserAuctions(auctionDTO.minecraftUuid, false)!!.size
        assertEquals(amount, 0)
        // Count auctions
        amount = dataSource.countPlayerAuctions(auctionDTO.minecraftUuid)!!
        assertEquals(amount, 1)
        // Delete and count auction
        dataSource.deleteAuction(auctionDTO)
        amount = dataSource.countPlayerAuctions(auctionDTO.minecraftUuid)!!
        assertEquals(amount, 0)
        val oldAuctionDTO = randomAuction.copy(time = 0)
        dataSource.insertAuction(oldAuctionDTO)
        amount = dataSource.getAuctionsOlderThan(System.currentTimeMillis() - 1)!!.size
        assertEquals(amount, 1)
    }
}
