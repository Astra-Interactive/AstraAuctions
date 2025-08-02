@file:Suppress("MaxLineLength", "LongParameterList")

package ru.astrainteractive.astramarket.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astralibs.string.replace

@Serializable
data class PluginTranslation(
    @SerialName("menu")
    val menu: Menu = Menu(),
    @SerialName("auction")
    val auction: Auction = Auction(),
    @SerialName("general")
    val general: General = General()
) {

    @Serializable
    class Menu(
        @SerialName("enabled_color")
        val enabledColor: StringDesc.Raw = StringDesc.Raw("&6"),
        @SerialName("disabled_color")
        val disabledColor: StringDesc.Raw = StringDesc.Raw("&f"),
        @SerialName("market")
        val market: StringDesc.Raw = StringDesc.Raw("&6Рынок"),
        @SerialName("filter")
        val filterExpired: StringDesc.Raw = StringDesc.Raw("&6Фильтр новизны"),
        @SerialName("expired")
        val expired: StringDesc.Raw = StringDesc.Raw("• Истекшие"),
        @SerialName("new")
        val new: StringDesc.Raw = StringDesc.Raw("• Новые"),
        @SerialName("next")
        val next: StringDesc.Raw = StringDesc.Raw("&6Дальше"),
        @SerialName("back")
        val back: StringDesc.Raw = StringDesc.Raw("&6Назад"),
        @SerialName("prev")
        val prev: StringDesc.Raw = StringDesc.Raw("&6Раньше"),
        @SerialName("sort")
        val sort: StringDesc.Raw = StringDesc.Raw("&6Сортировка"),
        @SerialName("slots_filter")
        val displayType: StringDesc.Raw = StringDesc.Raw("&6Тип отображения"),
        @SerialName("player_slots")
        val playerSlots: StringDesc.Raw = StringDesc.Raw("• По игрокам"),
        @SerialName("all_slots")
        val allSlots: StringDesc.Raw = StringDesc.Raw("• По предметам"),
    )

    @Serializable
    class Auction(
        @SerialName("wrong_item")
        val wrongItemInHand: StringDesc.Raw = PREFIX
            .plus("&#f55442Предмет в вашей руке не подходит для продажи")
            .toRaw(),
        @SerialName("wrong_price")
        val wrongPrice: StringDesc.Raw = PREFIX
            .plus("&#f55442Неверный ценовой диапазон")
            .toRaw(),
        @SerialName("slot_added")
        val auctionAdded: StringDesc.Raw = PREFIX
            .plus("&#18dbd1Предмет добавлен на аукцион")
            .toRaw(),
        @SerialName("inventory_full")
        val inventoryFull: StringDesc.Raw = PREFIX
            .plus("&#f55442Инвентарь полон")
            .toRaw(),
        @SerialName("owner_not_buyer")
        val ownerCantBeBuyer: StringDesc.Raw = PREFIX
            .plus("&#f55442Вы не можете купить собственный лот")
            .toRaw(),
        @SerialName("failed_to_pay")
        val failedToPay: StringDesc.Raw = PREFIX
            .plus("&#f55442Не удалось выплатить деньги")
            .toRaw(),
        @SerialName("notifyUserBuy")
        private val notifyUserBuy: StringDesc.Raw = PREFIX
            .plus(
                "&#18dbd1Вы купили предмет &#dbaa18%item%&#18dbd1 у игрока &#dbaa18%player_owner%&#18dbd1 за &#dbaa18%price%"
            )
            .toRaw(),
        @SerialName("notifyOwnerUserBuy")
        private val notifyOwnerUserBuy: StringDesc.Raw = PREFIX
            .plus("&#18dbd1Игрок &#dbaa18%player%&#18dbd1 купил у вас &#dbaa18%item%&#18dbd1 за &#dbaa18%price%")
            .toRaw(),
        @SerialName("not_enough_money")
        val notEnoughMoney: StringDesc.Raw = PREFIX
            .plus("&#f55442У вас недостаточно денег")
            .toRaw(),
        @SerialName("broadcast")
        private val broadcast: StringDesc.Raw = PREFIX
            .plus("&#18dbd1Игрок &#d6a213%player% &#18dbd1выставил на /market новый предмет")
            .toRaw(),
        @SerialName("player.auctions_amount")
        private val auctionsAmount: StringDesc.Raw = StringDesc.Raw("&7Количество: %amount%"),
        @SerialName("tab_completer.price")
        val tabCompleterPrice: StringDesc.Raw = StringDesc.Raw("ЦЕНА"),
        @SerialName("tab_completer.amount")
        val tabCompleterAmount: StringDesc.Raw = StringDesc.Raw("КОЛИЧЕСТВО"),
        @SerialName("auction_been_expired")
        val auctionHasBeenExpired: StringDesc.Raw = PREFIX
            .plus("&#d6a213Вы просрочили слот!")
            .toRaw(),
        @SerialName("left_button")
        val buySlot: StringDesc.Raw = StringDesc.Raw("&#d6a213ЛКМ &#18dbd1- купить"),
        @SerialName("right_button")
        val removeSlot: StringDesc.Raw = StringDesc.Raw("&#d6a213ПКМ &#18dbd1- убрать"),
        @SerialName("middle_click")
        val expireSlot: StringDesc.Raw = StringDesc.Raw("&#d6a213СКМ &#18dbd1- убрать в истёкшие"),
        @SerialName("not_auction_owner")
        val notAuctionOwner: StringDesc.Raw = PREFIX
            .plus("&#f55442Вы не владелец этого слота")
            .toRaw(),
        @SerialName("auction_deleted")
        val auctionDeleted: StringDesc.Raw = PREFIX
            .plus("&#f55442Слот удалён")
            .toRaw(),
        @SerialName("auction_expired_notify")
        private val notifyAuctionExpired: StringDesc.Raw = PREFIX
            .plus("&#f55442Ваш слот %item% за %price% только что был просрочен")
            .toRaw(),
        @SerialName("max_slots")
        val maxAuctions: StringDesc.Raw = PREFIX
            .plus("&#f55442У вас уже макстимальное число лотов")
            .toRaw(),
        @SerialName("auction_by")
        private val auctionBy: StringDesc.Raw = PREFIX
            .plus("&7Выставил: &#d6a213%player_owner%")
            .toRaw(),
        @SerialName("auction_created_ago")
        private val auctionCreatedAgo: StringDesc.Raw = StringDesc.Raw("&7Время: &#d6a213%time%"),
        @SerialName("auction_last")
        private val auctionLast: StringDesc.Raw = StringDesc.Raw("&7Последний слот: &#d6a213%time%"),
        @SerialName("auction_price")
        private val auctionPrice: StringDesc.Raw = StringDesc.Raw("&7Стоимость: &#d6a213%price%"),
        @SerialName("sort.asc")
        val sortAscSymbol: StringDesc.Raw = StringDesc.Raw(" &6&l↓"),
        @SerialName("sort.desc")
        val sortDescSymbol: StringDesc.Raw = StringDesc.Raw(" &6&l↑"),
        @SerialName("sort.material_desc")
        val sortMaterial: StringDesc.Raw = StringDesc.Raw("• По материалу"),
        @SerialName("sort.date_desc")
        val sortDate: StringDesc.Raw = StringDesc.Raw("• По дате"),
        @SerialName("sort.name_desc")
        val sortName: StringDesc.Raw = StringDesc.Raw("• По имени"),
        @SerialName("sort.amount_desc")
        val sortAmount: StringDesc.Raw = StringDesc.Raw("• По количеству"),
        @SerialName("sort.price_desc")
        val sortPrice: StringDesc.Raw = StringDesc.Raw("• По цене"),
        @SerialName("sort.player_asc")
        val sortPlayer: StringDesc.Raw = StringDesc.Raw("• По игроку"),
    ) {
        fun auctionPrice(price: Number): StringDesc {
            return auctionPrice
                .replace("%price%", "$price")
        }

        fun auctionLast(time: String): StringDesc {
            return auctionLast
                .replace("%time%", time)
        }

        fun auctionCreatedAgo(time: String): StringDesc {
            return auctionCreatedAgo
                .replace("%time%", time)
        }

        fun auctionBy(playerOwner: String): StringDesc {
            return auctionBy
                .replace("%player_owner%", playerOwner)
        }

        fun notifyAuctionExpired(item: String, price: Number): StringDesc {
            return notifyAuctionExpired
                .replace("%item%", item)
                .replace("%price%", "$price")
        }

        fun auctionsAmount(amount: Int): StringDesc {
            return auctionsAmount
                .replace("%amount%", "$amount")
        }

        fun broadcast(playerName: String): StringDesc {
            return broadcast
                .replace("%player%", playerName)
        }

        fun notifyOwnerUserBuy(playerName: String, itemName: String, price: Number): StringDesc {
            return notifyOwnerUserBuy
                .replace("%player%", playerName)
                .replace("%item%", itemName)
                .replace("%price%", "$price")
        }

        fun notifyUserBuy(playerOwner: String, itemName: String, price: Number): StringDesc {
            return notifyUserBuy
                .replace("%item%", itemName)
                .replace("%player_owner%", playerOwner)
                .replace("%price%", "$price")
        }
    }

    @Serializable
    class General(
        @SerialName("reload_started")
        val reloadStarted: StringDesc.Raw = PREFIX
            .plus("&#dbbb18Перезагрузка плагина")
            .toRaw(),
        @SerialName("reload_complete")
        val reloadSuccess: StringDesc.Raw = PREFIX
            .plus("&#42f596Перезагрузка успешно завершена")
            .toRaw(),
        @SerialName("no_permission")
        val noPermissions: StringDesc.Raw = PREFIX
            .plus("&#f55442У вас нет прав")
            .toRaw(),
        @SerialName("player_command")
        val onlyForPlayers: StringDesc.Raw = PREFIX
            .plus("&#f55442Эта команда только для игроков")
            .toRaw(),
        @SerialName("wrong_args")
        val wrongArgs: StringDesc.Raw = PREFIX
            .plus("&#f55442Неверное использование команды")
            .toRaw(),
        @SerialName("db_error")
        val dbError: StringDesc.Raw = PREFIX
            .plus("&#f55442Произошла ошибка")
            .toRaw(),
        @SerialName("error")
        val unexpectedError: StringDesc.Raw = PREFIX
            .plus("&#f55442Произошла непредвиденная ошибка")
            .toRaw(),
        @SerialName("time_format")
        val timeAgoFormat: StringDesc.Raw = StringDesc.Raw("%days%дн. %hours%ч. %minutes%м. назад")
    )

    companion object {
        private val PREFIX = StringDesc.Raw("&7[&6MARKET&7] ")
    }
}

private fun StringDesc.toRaw(): StringDesc.Raw {
    return StringDesc.Raw(this.raw)
}
