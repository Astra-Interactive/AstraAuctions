import com.astrainteractive.astramarket.domain.api.AuctionsAPIImpl
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import kotlinx.coroutines.runBlocking
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import java.util.*
import kotlin.random.Random
import kotlin.test.*

class AuctionsTests : ORMTest(builder = { DefaultDatabase(DBConnection.SQLite("db.db"), DBSyntax.SQLite) }) {
    private val dataSource: AuctionsAPI
        get() = AuctionsAPIImpl(assertConnected())
    val randomAuction: AuctionDTO
        get() = AuctionDTO(
            id = -1,
            discordId = UUID.randomUUID().toString(),
            minecraftUuid = UUID.randomUUID().toString(),
            time = System.currentTimeMillis(),
            item = Serializer.Wrapper.ByteArray(ByteArray(0)),
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