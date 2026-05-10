package ru.astrainteractive.astramarket.api.market.impl

import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.nio.file.Files

internal class TestContext {
    private val tempDir = Files.createTempDirectory("astra-market-test").toFile()
    val database = Database.connect(
        url = "jdbc:h2:${tempDir.absolutePath}/test",
        driver = "org.h2.Driver"
    )
    val marketApi = ExposedMarketApi(
        databaseFlow = flowOf(database),
        dispatchers = DefaultKotlinDispatchers
    )

    fun onStart() {
        transaction(database) {
            SchemaUtils.create(AuctionTable)
        }
    }

    fun onDisable() {
        tempDir.deleteRecursively()
    }

    fun createMarketSlot(
        uuid: String = "550e8400-e29b-41d4-a716-446655440000",
        username: String = "TestPlayer",
        price: Float = 100.0f,
        expired: Boolean = false,
        timeOffset: Long = 0L
    ): MarketSlot = MarketSlot(
        id = 0,
        minecraftUuid = uuid,
        minecraftUsername = username,
        time = System.currentTimeMillis() + timeOffset,
        item = EncodedObject.ByteArray("test_item".toByteArray()),
        price = price,
        expired = expired
    )
}
