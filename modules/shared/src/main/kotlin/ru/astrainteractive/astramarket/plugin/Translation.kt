@file:Suppress("MaxLineLength", "LongParameterList")

package ru.astrainteractive.astramarket.plugin

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.com.google.gson.annotations.SerializedName
import ru.astrainteractive.astralibs.string.StringDesc

@Serializable
data class Translation(
    val menu: Menu = Menu(),
    val auction: Auction = Auction(),
    val general: General = General()
) {
    @Serializable
    class Menu(
        @SerializedName("menu.title")
        val title: StringDesc.Raw = StringDesc.Raw("&#1382d6Аукцион"),
        @SerializedName("menu.title_expired")
        val expiredTitle: StringDesc.Raw = StringDesc.Raw("&#1382d6Истекшие"),
        @SerializedName("menu.back")
        val back: StringDesc.Raw = StringDesc.Raw("&#18dbd1Назад"),
        @SerializedName("menu.prev")
        val prev: StringDesc.Raw = StringDesc.Raw("&#18dbd1Раньше"),
        @SerializedName("menu.aauc")
        val aauc: StringDesc.Raw = StringDesc.Raw("&#18dbd1Аукцион"),
        @SerializedName("menu.expired")
        val expired: StringDesc.Raw = StringDesc.Raw("&#18dbd1Истекшие"),
        @SerializedName("menu.next")
        val next: StringDesc.Raw = StringDesc.Raw("&#18dbd1Дальше"),
        @SerializedName("menu.sort")
        val sort: StringDesc.Raw = StringDesc.Raw("&#18dbd1Сортировка"),
    )

    @Serializable
    class Auction(
        @SerializedName("auction.wrong_item")
        val wrongItemInHand: StringDesc.Raw = StringDesc.Raw("&#f55442Предмет в вашей руке не подходит для продажи"),
        @SerializedName("auction.wrong_price")
        val wrongPrice: StringDesc.Raw = StringDesc.Raw("&#f55442Неверный ценовой диапазон"),
        @SerializedName("auction.slot_added")
        val auctionAdded: StringDesc.Raw = StringDesc.Raw("&#18dbd1Предмет добавлен на аукцион"),
        @SerializedName("auction.inventory_full")
        val inventoryFull: StringDesc.Raw = StringDesc.Raw("&#f55442Инвентарь полон"),
        @SerializedName("auction.owner_not_buyer")
        val ownerCantBeBuyer: StringDesc.Raw = StringDesc.Raw("&#f55442Вы не можете купить собственный лот"),
        @SerializedName("auction.failed_to_pay")
        val failedToPay: StringDesc.Raw = StringDesc.Raw("&#f55442Не удалось выплатить деньги"),
        @SerializedName("auction.notifyUserBuy")
        val notifyUserBuy: StringDesc.Raw = StringDesc.Raw(
            "&#18dbd1Вы купили предмет &#dbaa18%item%&#18dbd1 у игрока &#dbaa18%player_owner%&#18dbd1 за &#dbaa18%price%"
        ),
        @SerializedName("auction.notifyOwnerUserBuy")
        val notifyOwnerUserBuy: StringDesc.Raw = StringDesc.Raw(
            "&#18dbd1Игрок &#dbaa18%player%&#18dbd1 купил у вас &#dbaa18%item%&#18dbd1 за &#dbaa18%price%"
        ),
        @SerializedName("auction.not_enough_money")
        val notEnoughMoney: StringDesc.Raw = StringDesc.Raw("&#f55442У вас недостаточно денег"),
        @SerializedName("auction.broadcast")
        val broadcast: StringDesc.Raw = StringDesc.Raw(
            "&#18dbd1Игрок &#d6a213%player% &#18dbd1выставил на /aauc новый предмет"
        ),
        @SerializedName("auction.tab_completer.price")
        val tabCompleterPrice: StringDesc.Raw = StringDesc.Raw("ЦЕНА"),
        @SerializedName("auction.tab_completer.amount")
        val tabCompleterAmount: StringDesc.Raw = StringDesc.Raw("КОЛИЧЕСТВО"),
        @SerializedName("auction.auction_been_expired")
        val auctionHasBeenExpired: StringDesc.Raw = StringDesc.Raw("&#d6a213Вы просрочили слот!"),
        @SerializedName("menu.left_button")
        val leftButton: StringDesc.Raw = StringDesc.Raw("&#d6a213ЛКМ &#18dbd1- купить"),
        @SerializedName("menu.right_button")
        val rightButton: StringDesc.Raw = StringDesc.Raw("&#d6a213ПКМ &#18dbd1- убрать"),
        @SerializedName("menu.middle_click")
        val middleClick: StringDesc.Raw = StringDesc.Raw("&#d6a213СКМ &#18dbd1- убрать в истёкшие"),
        @SerializedName("menu.not_auction_owner")
        val notAuctionOwner: StringDesc.Raw = StringDesc.Raw("&#f55442Вы не владелец этого слота"),
        @SerializedName("menu.auction_deleted") val auctionDeleted: StringDesc.Raw = StringDesc.Raw(
            "&#f55442Слот удалён"
        ),
        @SerializedName("menu.auction_expired_notify")
        val notifyAuctionExpired: StringDesc.Raw = StringDesc.Raw(
            "&#f55442Ваш слот %item% за %price% только что был просрочен"
        ),
        @SerializedName("auction.max_slots")
        val maxAuctions: StringDesc.Raw = StringDesc.Raw("&#f55442У вас уже макстимальное число лотов"),
        @SerializedName("auction.auction_by")
        val auctionBy: StringDesc.Raw = StringDesc.Raw("&7Выставил: &#d6a213%player_owner%"),
        @SerializedName("auction.auction_created_ago")
        val auctionCreatedAgo: StringDesc.Raw = StringDesc.Raw("&7Время: &#d6a213%time%"),
        @SerializedName("auction.auction_price")
        val auctionPrice: StringDesc.Raw = StringDesc.Raw("&7Стоимость: &#d6a213%price%"),
        @SerializedName("auction.sort.material_desc")
        val sortMaterialDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо материалу &#d6a213&l↑"),
        @SerializedName("auction.sort.material_asc")
        val sortMaterialAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо материалу &#d6a213&l↓"),
        @SerializedName("auction.sort.date_desc")
        val sortDateDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо дате &#d6a213&l↑"),
        @SerializedName("auction.sort.date_asc")
        val sortDateAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо дате &#d6a213&l↓"),
        @SerializedName("auction.sort.name_desc")
        val sortNameDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо имени &#d6a213&l↑"),
        @SerializedName("auction.sort.name_asc")
        val sortNameAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо имени &#d6a213&l↓"),
        @SerializedName("auction.sort.price_desc")
        val sortPriceDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо цене &#d6a213&l↑"),
        @SerializedName("auction.sort.price_asc")
        val sortPriceAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо цене &#d6a213&l↓"),
        @SerializedName("auction.sort.player_desc")
        val sortPlayerDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо игроку &#d6a213&l↑"),
        @SerializedName("auction.sort.player_asc")
        val sortPlayerAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо игроку &#d6a213&l↓"),
    )

    @Serializable
    class General(
        @SerializedName("general.reload_started")
        val reloadStarted: StringDesc.Raw = StringDesc.Raw("&#dbbb18Перезагрузка плагина"),
        @SerializedName("general.reload_complete")
        val reloadSuccess: StringDesc.Raw = StringDesc.Raw("&#42f596Перезагрузка успешно завершена"),
        @SerializedName("general.no_permission")
        val noPermissions: StringDesc.Raw = StringDesc.Raw("&#f55442У вас нет прав"),
        @SerializedName("general.player_command")
        val onlyForPlayers: StringDesc.Raw = StringDesc.Raw("&#f55442Эта команда только для игроков"),
        @SerializedName("general.wrong_args")
        val wrongArgs: StringDesc.Raw = StringDesc.Raw("&#f55442Неверное использование команды"),
        @SerializedName("general.db_error")
        val dbError: StringDesc.Raw = StringDesc.Raw("&#f55442Произошла ошибка"),
        @SerializedName("general.error")
        val unexpectedError: StringDesc.Raw = StringDesc.Raw("&#f55442Произошла непредвиденная ошибка"),
        @SerializedName("general.time_format")
        val timeAgoFormat: StringDesc.Raw = StringDesc.Raw("%days%дн. %hours%ч. %minutes%м. назад")
    )
}
