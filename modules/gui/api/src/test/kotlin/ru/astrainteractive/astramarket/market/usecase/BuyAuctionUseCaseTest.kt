@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.market.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.data.dao.market.fake.FakeMarketRepository
import ru.astrainteractive.astramarket.market.fake.FakeCurrencyEconomyProviderResolver
import ru.astrainteractive.astramarket.market.fake.FakeEconomyFacade
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BuyAuctionUseCaseTest {
    private val sellerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    private val auction = MarketTestContext.slot(
        id = 1,
        owner = sellerId,
        price = 100f
    )
    private val context = MarketTestContext(config = PluginConfig(), slots = listOf(auction))
    private val economy = FakeEconomyFacade(balances = mapOf(context.buyer to 150.0, context.seller to 0.0))

    private fun useCase(economyFacade: FakeEconomyFacade? = economy): BuyAuctionUseCase {
        return BuyAuctionUseCase(
            auctionsBridge = context.auctionsBridge,
            marketRepository = context.marketRepository,
            playerInteractionBridge = context.playerInteractionBridge,
            pluginTranslationKrate = context.translationKrate,
            configKrate = context.configKrate,
            economyProviderFactory = FakeCurrencyEconomyProviderResolver(economyFacade)
        )
    }

    @Test
    fun GIVEN_buyer_with_enough_money_WHEN_auction_is_bought_THEN_money_moves_and_item_is_given(): TestResult {
        return runTest {
            val isBought = useCase().invoke(auction, context.buyer)

            assertTrue(isBought)
            assertEquals(50.0, economy.balanceOf(context.buyer))
            assertEquals(100.0, economy.balanceOf(context.seller))
            assertTrue(context.marketRepository.slots.isEmpty())
            assertEquals(context.buyer, context.auctionsBridge.itemsGiven.single().receiver)
            assertEquals(1, context.playerInteractionBridge.messagesTo(context.seller).size)
            assertEquals(listOf(context.config.sounds.sold), context.playerInteractionBridge.soundsFor(context.buyer))
        }
    }

    @Test
    fun GIVEN_no_economy_WHEN_auction_is_bought_THEN_nothing_happens(): TestResult {
        return runTest {
            val isBought = useCase(economyFacade = null).invoke(auction, context.buyer)

            assertFalse(isBought)
            assertEquals(listOf(auction), context.marketRepository.slots)
            assertTrue(context.playerInteractionBridge.messages.isEmpty())
        }
    }

    @Test
    fun GIVEN_auction_already_gone_WHEN_auction_is_bought_THEN_nothing_happens(): TestResult {
        return runTest {
            context.marketRepository.deleteSlot(auction)

            val isBought = useCase().invoke(auction, context.buyer)

            assertFalse(isBought)
            assertEquals(150.0, economy.balanceOf(context.buyer))
        }
    }

    @Test
    fun GIVEN_own_auction_WHEN_auction_is_bought_THEN_player_reads_own_lot(): TestResult {
        return runTest {
            val isBought = useCase().invoke(auction, context.seller)

            assertFalse(isBought)
            assertEquals(
                listOf(context.translation.buy.ownLot),
                context.playerInteractionBridge.messagesTo(context.seller)
            )
        }
    }

    @Test
    fun GIVEN_full_inventory_WHEN_auction_is_bought_THEN_player_reads_inventory_full(): TestResult {
        return runTest {
            context.auctionsBridge.isInventoryFull = true

            val isBought = useCase().invoke(auction, context.buyer)

            assertFalse(isBought)
            val messages = context.playerInteractionBridge.messagesTo(context.buyer)
            assertEquals(listOf(context.translation.error.inventoryFull), messages)
            assertEquals(listOf(context.config.sounds.fail), context.playerInteractionBridge.soundsFor(context.buyer))
        }
    }

    @Test
    fun GIVEN_buyer_without_money_WHEN_auction_is_bought_THEN_player_reads_not_enough_money(): TestResult {
        return runTest {
            val poorEconomy = FakeEconomyFacade(balances = mapOf(context.buyer to 10.0))

            val isBought = useCase(poorEconomy).invoke(auction, context.buyer)

            assertFalse(isBought)
            val messages = context.playerInteractionBridge.messagesTo(context.buyer)
            assertEquals(listOf(context.translation.buy.notEnoughMoney), messages)
            assertEquals(10.0, poorEconomy.balanceOf(context.buyer))
        }
    }

    @Test
    fun GIVEN_payment_to_seller_fails_WHEN_auction_is_bought_THEN_buyer_gets_money_back(): TestResult {
        return runTest {
            economy.failingDeposits += context.seller

            val isBought = useCase().invoke(auction, context.buyer)

            assertFalse(isBought)
            assertEquals(150.0, economy.balanceOf(context.buyer))
            val messages = context.playerInteractionBridge.messagesTo(context.buyer)
            assertEquals(listOf(context.translation.buy.paymentFailed), messages)
            assertEquals(listOf(auction), context.marketRepository.slots)
        }
    }

    @Test
    fun GIVEN_storage_fails_on_delete_WHEN_auction_is_bought_THEN_money_is_returned(): TestResult {
        return runTest {
            context.marketRepository.failingOperations += FakeMarketRepository.Operation.DELETE

            val isBought = useCase().invoke(auction, context.buyer)

            assertFalse(isBought)
            assertEquals(150.0, economy.balanceOf(context.buyer))
            assertEquals(0.0, economy.balanceOf(context.seller))
            val messages = context.playerInteractionBridge.messagesTo(context.buyer)
            assertEquals(listOf(context.translation.error.database), messages)
            assertTrue(context.auctionsBridge.itemsGiven.isEmpty())
        }
    }
}
