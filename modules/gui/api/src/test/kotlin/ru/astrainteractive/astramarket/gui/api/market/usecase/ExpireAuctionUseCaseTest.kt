@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.gui.api.market.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.data.dao.market.fake.FakeMarketRepository
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpireAuctionUseCaseTest {
    private val sellerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    private val auction = MarketTestContext.slot(
        id = 1,
        owner = sellerId,
        price = 100f
    )
    private val context = MarketTestContext(config = PluginConfig(), slots = listOf(auction))
    private val moderator = context.buyer
    private val useCase = ExpireAuctionUseCase(
        auctionsBridge = context.auctionsBridge,
        marketRepository = context.marketRepository,
        playerInteractionBridge = context.playerInteractionBridge,
        pluginTranslationKrate = context.translationKrate
    )

    @Test
    fun GIVEN_moderator_WHEN_auction_is_expired_THEN_it_expires_and_owner_is_noticed(): TestResult {
        return runTest {
            context.auctionsBridge.hasExpirePermission = true

            val isExpired = useCase.invoke(auction, moderator)

            assertTrue(isExpired)
            assertTrue(context.marketRepository.slots.single().expired)
            assertEquals(1, context.playerInteractionBridge.messagesTo(context.seller).size)
            assertEquals(
                listOf(context.translation.expire.success),
                context.playerInteractionBridge.messagesTo(moderator)
            )
        }
    }

    @Test
    fun GIVEN_player_without_permission_WHEN_auction_is_expired_THEN_player_reads_no_permission(): TestResult {
        return runTest {
            val isExpired = useCase.invoke(auction, moderator)

            assertFalse(isExpired)
            val messages = context.playerInteractionBridge.messagesTo(moderator)
            assertEquals(listOf(context.translation.error.noPermission), messages)
            assertFalse(context.marketRepository.slots.single().expired)
        }
    }

    @Test
    fun GIVEN_auction_already_gone_WHEN_auction_is_expired_THEN_nothing_happens(): TestResult {
        return runTest {
            context.auctionsBridge.hasExpirePermission = true
            context.marketRepository.deleteSlot(auction)

            val isExpired = useCase.invoke(auction, moderator)

            assertFalse(isExpired)
            assertTrue(context.playerInteractionBridge.messages.isEmpty())
        }
    }

    @Test
    fun GIVEN_storage_fails_WHEN_auction_is_expired_THEN_moderator_reads_unexpected_error(): TestResult {
        return runTest {
            context.auctionsBridge.hasExpirePermission = true
            context.marketRepository.failingOperations += FakeMarketRepository.Operation.EXPIRE

            val isExpired = useCase.invoke(auction, moderator)

            assertFalse(isExpired)
            assertEquals(
                listOf(context.translation.error.unexpected),
                context.playerInteractionBridge.messagesTo(moderator)
            )
        }
    }
}
