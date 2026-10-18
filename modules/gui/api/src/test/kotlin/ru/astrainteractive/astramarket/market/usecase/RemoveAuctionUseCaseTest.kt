@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.market.usecase

import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.data.dao.market.fake.FakeMarketRepository
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemoveAuctionUseCaseTest {
    private val sellerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    private val auction = MarketTestContext.slot(id = 1, owner = sellerId, price = 100f)
    private val context = MarketTestContext(config = PluginConfig(), slots = listOf(auction))
    private val useCase = RemoveAuctionUseCase(
        auctionsBridge = context.auctionsBridge,
        marketRepository = context.marketRepository,
        playerInteractionBridge = context.playerInteractionBridge,
        pluginTranslationKrate = context.translationKrate,
        configKrate = context.configKrate
    )

    @Test
    fun GIVEN_owner_WHEN_auction_is_removed_THEN_item_returns_to_owner() = runTest {
        val isRemoved = useCase.invoke(auction, context.seller)

        assertTrue(isRemoved)
        assertTrue(context.marketRepository.slots.isEmpty())
        assertEquals(context.seller, context.auctionsBridge.itemsGiven.single().receiver)
        assertEquals(
            listOf(context.translation.remove.removed),
            context.playerInteractionBridge.messagesTo(context.seller)
        )
    }

    @Test
    fun GIVEN_other_player_WHEN_auction_is_removed_THEN_player_reads_not_owner() = runTest {
        val isRemoved = useCase.invoke(auction, context.buyer)

        assertFalse(isRemoved)
        assertEquals(
            listOf(context.translation.remove.notOwner),
            context.playerInteractionBridge.messagesTo(context.buyer)
        )
        assertEquals(listOf(auction), context.marketRepository.slots)
    }

    @Test
    fun GIVEN_auction_already_gone_WHEN_auction_is_removed_THEN_nothing_happens() = runTest {
        context.marketRepository.deleteSlot(auction)

        val isRemoved = useCase.invoke(auction, context.seller)

        assertFalse(isRemoved)
        assertTrue(context.playerInteractionBridge.messages.isEmpty())
    }

    @Test
    fun GIVEN_full_inventory_WHEN_auction_is_removed_THEN_owner_reads_inventory_full() = runTest {
        context.auctionsBridge.isInventoryFull = true

        val isRemoved = useCase.invoke(auction, context.seller)

        assertFalse(isRemoved)
        val messages = context.playerInteractionBridge.messagesTo(context.seller)
        assertEquals(listOf(context.translation.error.inventoryFull), messages)
        assertEquals(listOf(auction), context.marketRepository.slots)
    }

    @Test
    fun GIVEN_storage_fails_WHEN_auction_is_removed_THEN_owner_reads_unexpected_error() = runTest {
        context.marketRepository.failingOperations += FakeMarketRepository.Operation.DELETE

        val isRemoved = useCase.invoke(auction, context.seller)

        assertFalse(isRemoved)
        val messages = context.playerInteractionBridge.messagesTo(context.seller)
        assertEquals(listOf(context.translation.error.unexpected), messages)
        assertTrue(context.auctionsBridge.itemsGiven.isEmpty())
    }
}
