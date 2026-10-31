@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.gui.api.market.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.data.dao.market.fake.FakeMarketRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CreateAuctionUseCaseTest {
    private fun contextWith(config: PluginConfig = PluginConfig()): MarketTestContext {
        return MarketTestContext(
            config = config,
            slots = emptyList()
        )
    }

    private fun useCaseOf(context: MarketTestContext): CreateAuctionUseCase {
        return CreateAuctionUseCase(
            auctionsBridge = context.auctionsBridge,
            marketRepository = context.marketRepository,
            playerInteractionBridge = context.playerInteractionBridge,
            pluginTranslationKrate = context.translationKrate,
            configKrate = context.configKrate
        )
    }

    @Test
    fun GIVEN_valid_lot_WHEN_auction_is_created_THEN_it_is_listed_and_announced(): TestResult {
        return runTest {
            val context = contextWith()
            val lot = MarketTestContext.slot(
                id = 0,
                owner = context.seller,
                price = 100f
            )

            val isCreated = useCaseOf(context).invoke(lot, context.seller)

            assertTrue(isCreated)
            assertEquals(1, context.marketRepository.slots.size)
            assertEquals(
                listOf(context.translation.sell.listed),
                context.playerInteractionBridge.messagesTo(context.seller)
            )
            assertEquals(1, context.playerInteractionBridge.broadcasts.size)
        }
    }

    @Test
    fun GIVEN_announcements_off_WHEN_auction_is_created_THEN_nothing_is_broadcast(): TestResult {
        return runTest {
            val context = contextWith(PluginConfig(auction = PluginConfig.Auction(announce = false)))
            val lot = MarketTestContext.slot(
                id = 0,
                owner = context.seller,
                price = 100f
            )

            val isCreated = useCaseOf(context).invoke(lot, context.seller)

            assertTrue(isCreated)
            assertTrue(context.playerInteractionBridge.broadcasts.isEmpty())
        }
    }

    @Test
    fun GIVEN_invalid_item_WHEN_auction_is_created_THEN_player_reads_wrong_item(): TestResult {
        return runTest {
            val context = contextWith()
            context.auctionsBridge.isItemValid = false
            val lot = MarketTestContext.slot(
                id = 0,
                owner = context.seller,
                price = 100f
            )

            val isCreated = useCaseOf(context).invoke(lot, context.seller)

            assertFalse(isCreated)
            val messages = context.playerInteractionBridge.messagesTo(context.seller)
            assertEquals(listOf(context.translation.sell.wrongItem), messages)
            assertTrue(context.marketRepository.slots.isEmpty())
        }
    }

    @Test
    fun GIVEN_player_at_permission_limit_WHEN_auction_is_created_THEN_player_reads_too_many_lots(): TestResult {
        return runTest {
            val context = contextWith()
            context.auctionsBridge.permissionLimit = 1
            val lot = MarketTestContext.slot(
                id = 0,
                owner = context.seller,
                price = 100f
            )
            context.marketRepository.insertSlot(lot)

            val isCreated = useCaseOf(context).invoke(lot, context.seller)

            assertFalse(isCreated)
            val messages = context.playerInteractionBridge.messagesTo(context.seller)
            assertEquals(listOf(context.translation.sell.tooManyLots), messages)
            assertEquals(1, context.marketRepository.slots.size)
        }
    }

    @Test
    fun GIVEN_price_out_of_range_WHEN_auction_is_created_THEN_player_reads_wrong_price(): TestResult {
        return runTest {
            val context = contextWith()
            val lot = MarketTestContext.slot(
                id = 0,
                owner = context.seller,
                price = 1f
            )

            val isCreated = useCaseOf(context).invoke(lot, context.seller)

            assertFalse(isCreated)
            val messages = context.playerInteractionBridge.messagesTo(context.seller)
            assertEquals(listOf(context.translation.sell.wrongPrice), messages)
        }
    }

    @Test
    fun GIVEN_storage_fails_WHEN_auction_is_created_THEN_player_reads_database_error(): TestResult {
        return runTest {
            val context = contextWith()
            context.marketRepository.failingOperations += FakeMarketRepository.Operation.INSERT
            val lot = MarketTestContext.slot(
                id = 0,
                owner = context.seller,
                price = 100f
            )

            val isCreated = useCaseOf(context).invoke(lot, context.seller)

            assertFalse(isCreated)
            val messages = context.playerInteractionBridge.messagesTo(context.seller)
            assertEquals(listOf(context.translation.error.database), messages)
            assertEquals(listOf(context.config.sounds.fail), context.playerInteractionBridge.soundsFor(context.seller))
        }
    }
}
