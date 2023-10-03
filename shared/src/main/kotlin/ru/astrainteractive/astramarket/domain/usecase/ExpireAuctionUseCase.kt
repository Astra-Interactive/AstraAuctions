package ru.astrainteractive.astramarket.domain.usecase

import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.data.AuctionsRepository
import ru.astrainteractive.astramarket.domain.data.PlayerInteraction
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
interface ExpireAuctionUseCase : UseCase.Parametrized<ExpireAuctionUseCase.Params, Boolean> {
    class Params(
        val auction: AuctionDTO,
        val playerUUID: UUID
    )
}

internal class ExpireAuctionUseCaseImpl(
    private val auctionsRepository: AuctionsRepository,
    private val playerInteraction: PlayerInteraction,
    private val translation: Translation,
) : ExpireAuctionUseCase {

    override suspend operator fun invoke(input: ExpireAuctionUseCase.Params): Boolean {
        val playerUUID = input.playerUUID
        val receivedAuction = input.auction
        val ownerUUID = receivedAuction.minecraftUuid.let(UUID::fromString)

        if (!auctionsRepository.hasExpirePermission(playerUUID)) {
            playerInteraction.sendTranslationMessage(playerUUID) { translation.general.noPermissions }
            return false
        }
        val auction = auctionsRepository.getAuctionOrNull(receivedAuction.id) ?: return false
        val itemName = auctionsRepository.itemDesc(auction)
        playerInteraction.sendTranslationMessage(ownerUUID) {
            translation.auction.notifyAuctionExpired
                .replace("%item%", itemName)
                .replace("%price%", auction.price.toString())
        }

        val result = auctionsRepository.expireAuction(auction)
        if (result == null) {
            playerInteraction.sendTranslationMessage(playerUUID) {
                translation.general.unexpectedError
            }
        } else {
            playerInteraction.sendTranslationMessage(playerUUID) {
                translation.auction.auctionHasBeenExpired
            }
        }
        return (result != null)
    }
}
