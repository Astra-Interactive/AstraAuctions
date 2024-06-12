@file:Suppress("MaxLineLength", "LongParameterList")

package ru.astrainteractive.astramarket.core

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.com.google.gson.annotations.SerializedName
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.StringDescExt.replace

@Serializable
data class Translation(
    @SerializedName("menu")
    val menu: Menu = Menu(),
    @SerializedName("auction")
    val auction: Auction = Auction(),
    @SerializedName("general")
    val general: General = General()
) {

    @Serializable
    class Menu(
        @SerializedName("market")
        val market: StringDesc.Raw = StringDesc.Raw("&#18dbd1Рынок"),
        @SerializedName("expired")
        val expired: StringDesc.Raw = StringDesc.Raw("&#18dbd1Истекшие"),
        @SerializedName("new")
        val new: StringDesc.Raw = StringDesc.Raw("&#18dbd1Новые"),
        @SerializedName("next")
        val next: StringDesc.Raw = StringDesc.Raw("&#18dbd1Дальше"),
        @SerializedName("back")
        val back: StringDesc.Raw = StringDesc.Raw("&#18dbd1Назад"),
        @SerializedName("prev")
        val prev: StringDesc.Raw = StringDesc.Raw("&#18dbd1Раньше"),
        @SerializedName("sort")
        val sort: StringDesc.Raw = StringDesc.Raw("&#18dbd1Сортировка"),
    )

    @Serializable
    class Auction(
        @SerializedName("wrong_item")
        val wrongItemInHand: StringDesc.Raw = StringDesc.Raw("&#f55442Предмет в вашей руке не подходит для продажи"),
        @SerializedName("wrong_price")
        val wrongPrice: StringDesc.Raw = StringDesc.Raw("&#f55442Неверный ценовой диапазон"),
        @SerializedName("slot_added")
        val auctionAdded: StringDesc.Raw = StringDesc.Raw("&#18dbd1Предмет добавлен на аукцион"),
        @SerializedName("inventory_full")
        val inventoryFull: StringDesc.Raw = StringDesc.Raw("&#f55442Инвентарь полон"),
        @SerializedName("owner_not_buyer")
        val ownerCantBeBuyer: StringDesc.Raw = StringDesc.Raw("&#f55442Вы не можете купить собственный лот"),
        @SerializedName("failed_to_pay")
        val failedToPay: StringDesc.Raw = StringDesc.Raw("&#f55442Не удалось выплатить деньги"),
        @SerializedName("notifyUserBuy")
        private val notifyUserBuy: StringDesc.Raw = StringDesc.Raw(
            "&#18dbd1Вы купили предмет &#dbaa18%item%&#18dbd1 у игрока &#dbaa18%player_owner%&#18dbd1 за &#dbaa18%price%"
        ),
        @SerializedName("notifyOwnerUserBuy")
        private val notifyOwnerUserBuy: StringDesc.Raw = StringDesc.Raw(
            "&#18dbd1Игрок &#dbaa18%player%&#18dbd1 купил у вас &#dbaa18%item%&#18dbd1 за &#dbaa18%price%"
        ),
        @SerializedName("not_enough_money")
        val notEnoughMoney: StringDesc.Raw = StringDesc.Raw("&#f55442У вас недостаточно денег"),
        @SerializedName("broadcast")
        private val broadcast: StringDesc.Raw = StringDesc.Raw(
            "&#18dbd1Игрок &#d6a213%player% &#18dbd1выставил на /aauc новый предмет"
        ),
        @SerializedName("menu.player.auctions_amount")
        private val auctionsAmount: StringDesc.Raw = StringDesc.Raw("&7Количество: %amount%"),
        @SerializedName("tab_completer.price")
        val tabCompleterPrice: StringDesc.Raw = StringDesc.Raw("ЦЕНА"),
        @SerializedName("tab_completer.amount")
        val tabCompleterAmount: StringDesc.Raw = StringDesc.Raw("КОЛИЧЕСТВО"),
        @SerializedName("auction_been_expired")
        val auctionHasBeenExpired: StringDesc.Raw = StringDesc.Raw("&#d6a213Вы просрочили слот!"),
        @SerializedName("menu.left_button")
        val leftButton: StringDesc.Raw = StringDesc.Raw("&#d6a213ЛКМ &#18dbd1- купить"),
        @SerializedName("menu.right_button")
        val rightButton: StringDesc.Raw = StringDesc.Raw("&#d6a213ПКМ &#18dbd1- убрать"),
        @SerializedName("menu.middle_click")
        val middleClick: StringDesc.Raw = StringDesc.Raw("&#d6a213СКМ &#18dbd1- убрать в истёкшие"),
        @SerializedName("menu.not_auction_owner")
        val notAuctionOwner: StringDesc.Raw = StringDesc.Raw("&#f55442Вы не владелец этого слота"),
        @SerializedName("menu.auction_deleted")
        val auctionDeleted: StringDesc.Raw = StringDesc.Raw("&#f55442Слот удалён"),
        @SerializedName("menu.auction_expired_notify")
        private val notifyAuctionExpired: StringDesc.Raw = StringDesc.Raw(
            "&#f55442Ваш слот %item% за %price% только что был просрочен"
        ),
        @SerializedName("max_slots")
        val maxAuctions: StringDesc.Raw = StringDesc.Raw("&#f55442У вас уже макстимальное число лотов"),
        @SerializedName("auction_by")
        private val auctionBy: StringDesc.Raw = StringDesc.Raw("&7Выставил: &#d6a213%player_owner%"),
        @SerializedName("auction_created_ago")
        private val auctionCreatedAgo: StringDesc.Raw = StringDesc.Raw("&7Время: &#d6a213%time%"),
        @SerializedName("auction_last")
        private val auctionLast: StringDesc.Raw = StringDesc.Raw("&7Последний слот: &#d6a213%time%"),
        @SerializedName("auction_price")
        private val auctionPrice: StringDesc.Raw = StringDesc.Raw("&7Стоимость: &#d6a213%price%"),
        @SerializedName("sort.material_desc")
        val sortMaterialDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо материалу &#d6a213&l↑"),
        @SerializedName("sort.material_asc")
        val sortMaterialAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо материалу &#d6a213&l↓"),
        @SerializedName("sort.date_desc")
        val sortDateDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо дате &#d6a213&l↑"),
        @SerializedName("sort.date_asc")
        val sortDateAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо дате &#d6a213&l↓"),
        @SerializedName("sort.name_desc")
        val sortNameDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо имени &#d6a213&l↑"),
        @SerializedName("sort.amount_asc")
        val sortAmountAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо количеству &#d6a213&l↓"),
        @SerializedName("sort.amount_desc")
        val sortAmountDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо количеству &#d6a213&l↑"),
        @SerializedName("sort.name_asc")
        val sortNameAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо имени &#d6a213&l↓"),
        @SerializedName("sort.price_desc")
        val sortPriceDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо цене &#d6a213&l↑"),
        @SerializedName("sort.price_asc")
        val sortPriceAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо цене &#d6a213&l↓"),
        @SerializedName("sort.player_desc")
        val sortPlayerDesc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо игроку &#d6a213&l↑"),
        @SerializedName("sort.player_asc")
        val sortPlayerAsc: StringDesc.Raw = StringDesc.Raw("&#FFFFFFпо игроку &#d6a213&l↓"),
    ) {
        fun auctionPrice(price: Number): StringDesc.Raw {
            return auctionPrice
                .replace("%price%", "$price")
        }

        fun auctionLast(time: String): StringDesc.Raw {
            return auctionLast
                .replace("%pricetime%", time)
        }

        fun auctionCreatedAgo(time: String): StringDesc.Raw {
            return auctionCreatedAgo
                .replace("%pricetime%", time)
        }

        fun auctionBy(playerOwner: String): StringDesc.Raw {
            return auctionBy
                .replace("%player_owner%", playerOwner)
        }

        fun notifyAuctionExpired(item: String, price: Number): StringDesc.Raw {
            return notifyAuctionExpired
                .replace("%item%", item)
                .replace("%price%", "$price")
        }

        fun auctionsAmount(amount: Int): StringDesc.Raw {
            return auctionsAmount
                .replace("%amount%", "$amount")
        }

        fun broadcast(playerName: String): StringDesc.Raw {
            return broadcast
                .replace("%player%", playerName)
        }

        fun notifyOwnerUserBuy(playerName: String, itemName: String, price: Number): StringDesc.Raw {
            return notifyOwnerUserBuy
                .replace("%player%", playerName)
                .replace("%item%", itemName)
                .replace("%price%", "$price")
        }

        fun notifyUserBuy(playerOwner: String, itemName: String, price: Number): StringDesc.Raw {
            return notifyUserBuy
                .replace("%item%", itemName)
                .replace("%player_owner%", playerOwner)
                .replace("%price%", "$price")
        }
    }

    @Serializable
    class General(
        @SerializedName("reload_started")
        val reloadStarted: StringDesc.Raw = StringDesc.Raw("&#dbbb18Перезагрузка плагина"),
        @SerializedName("reload_complete")
        val reloadSuccess: StringDesc.Raw = StringDesc.Raw("&#42f596Перезагрузка успешно завершена"),
        @SerializedName("no_permission")
        val noPermissions: StringDesc.Raw = StringDesc.Raw("&#f55442У вас нет прав"),
        @SerializedName("player_command")
        val onlyForPlayers: StringDesc.Raw = StringDesc.Raw("&#f55442Эта команда только для игроков"),
        @SerializedName("wrong_args")
        val wrongArgs: StringDesc.Raw = StringDesc.Raw("&#f55442Неверное использование команды"),
        @SerializedName("db_error")
        val dbError: StringDesc.Raw = StringDesc.Raw("&#f55442Произошла ошибка"),
        @SerializedName("error")
        val unexpectedError: StringDesc.Raw = StringDesc.Raw("&#f55442Произошла непредвиденная ошибка"),
        @SerializedName("time_format")
        val timeAgoFormat: StringDesc.Raw = StringDesc.Raw("%days%дн. %hours%ч. %minutes%м. назад")
    ) {
        fun timeAgoFormat(days: Int, hours: Int, minutes: Int): StringDesc.Raw {
            return timeAgoFormat
                .replace("%days%", "$days")
                .replace("%hours%", "$hours")
                .replace("%minutes%", "$minutes")
        }
    }
}
