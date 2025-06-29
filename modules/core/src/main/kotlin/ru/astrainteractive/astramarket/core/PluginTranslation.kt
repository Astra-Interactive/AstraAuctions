@file:Suppress("MaxLineLength", "LongParameterList")

package ru.astrainteractive.astramarket.core

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.com.google.gson.annotations.SerializedName
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astralibs.string.replace

@Serializable
data class PluginTranslation(
    @SerializedName("menu")
    val menu: Menu = Menu(),
    @SerializedName("auction")
    val auction: Auction = Auction(),
    @SerializedName("general")
    val general: General = General()
) {

    @Serializable
    class Menu(
        @SerializedName("enabled_color")
        val enabledColor: StringDesc.Raw = StringDesc.Raw("&6"),
        @SerializedName("disabled_color")
        val disabledColor: StringDesc.Raw = StringDesc.Raw("&f"),
        @SerializedName("market")
        val market: StringDesc.Raw = StringDesc.Raw("&6Рынок"),
        @SerializedName("filter")
        val filterExpired: StringDesc.Raw = StringDesc.Raw("&6Фильтр новизны"),
        @SerializedName("expired")
        val expired: StringDesc.Raw = StringDesc.Raw("• Истекшие"),
        @SerializedName("new")
        val new: StringDesc.Raw = StringDesc.Raw("• Новые"),
        @SerializedName("next")
        val next: StringDesc.Raw = StringDesc.Raw("&6Дальше"),
        @SerializedName("back")
        val back: StringDesc.Raw = StringDesc.Raw("&6Назад"),
        @SerializedName("prev")
        val prev: StringDesc.Raw = StringDesc.Raw("&6Раньше"),
        @SerializedName("sort")
        val sort: StringDesc.Raw = StringDesc.Raw("&6Сортировка"),
        @SerializedName("slots_filter")
        val displayType: StringDesc.Raw = StringDesc.Raw("&6Тип отображения"),
        @SerializedName("player_slots")
        val playerSlots: StringDesc.Raw = StringDesc.Raw("• По игрокам"),
        @SerializedName("all_slots")
        val allSlots: StringDesc.Raw = StringDesc.Raw("• По предметам"),
    )

    @Serializable
    class Auction(
        @SerializedName("wrong_item")
        val wrongItemInHand: StringDesc.Raw = PREFIX
            .plus("&#f55442Предмет в вашей руке не подходит для продажи")
            .toRaw(),
        @SerializedName("wrong_price")
        val wrongPrice: StringDesc.Raw = PREFIX
            .plus("&#f55442Неверный ценовой диапазон")
            .toRaw(),
        @SerializedName("slot_added")
        val auctionAdded: StringDesc.Raw = PREFIX
            .plus("&#18dbd1Предмет добавлен на аукцион")
            .toRaw(),
        @SerializedName("inventory_full")
        val inventoryFull: StringDesc.Raw = PREFIX
            .plus("&#f55442Инвентарь полон")
            .toRaw(),
        @SerializedName("owner_not_buyer")
        val ownerCantBeBuyer: StringDesc.Raw = PREFIX
            .plus("&#f55442Вы не можете купить собственный лот")
            .toRaw(),
        @SerializedName("failed_to_pay")
        val failedToPay: StringDesc.Raw = PREFIX
            .plus("&#f55442Не удалось выплатить деньги")
            .toRaw(),
        @SerializedName("notifyUserBuy")
        private val notifyUserBuy: StringDesc.Raw = PREFIX
            .plus(
                "&#18dbd1Вы купили предмет &#dbaa18%item%&#18dbd1 у игрока &#dbaa18%player_owner%&#18dbd1 за &#dbaa18%price%"
            )
            .toRaw(),
        @SerializedName("notifyOwnerUserBuy")
        private val notifyOwnerUserBuy: StringDesc.Raw = PREFIX
            .plus("&#18dbd1Игрок &#dbaa18%player%&#18dbd1 купил у вас &#dbaa18%item%&#18dbd1 за &#dbaa18%price%")
            .toRaw(),
        @SerializedName("not_enough_money")
        val notEnoughMoney: StringDesc.Raw = PREFIX
            .plus("&#f55442У вас недостаточно денег")
            .toRaw(),
        @SerializedName("broadcast")
        private val broadcast: StringDesc.Raw = PREFIX
            .plus("&#18dbd1Игрок &#d6a213%player% &#18dbd1выставил на /aauc новый предмет")
            .toRaw(),
        @SerializedName("player.auctions_amount")
        private val auctionsAmount: StringDesc.Raw = PREFIX
            .plus("&7Количество: %amount%")
            .toRaw(),
        @SerializedName("tab_completer.price")
        val tabCompleterPrice: StringDesc.Raw = StringDesc.Raw("ЦЕНА"),
        @SerializedName("tab_completer.amount")
        val tabCompleterAmount: StringDesc.Raw = StringDesc.Raw("КОЛИЧЕСТВО"),
        @SerializedName("auction_been_expired")
        val auctionHasBeenExpired: StringDesc.Raw = PREFIX
            .plus("&#d6a213Вы просрочили слот!")
            .toRaw(),
        @SerializedName("left_button")
        val buySlot: StringDesc.Raw = StringDesc.Raw("&#d6a213ЛКМ &#18dbd1- купить"),
        @SerializedName("right_button")
        val removeSlot: StringDesc.Raw = StringDesc.Raw("&#d6a213ПКМ &#18dbd1- убрать"),
        @SerializedName("middle_click")
        val expireSlot: StringDesc.Raw = StringDesc.Raw("&#d6a213СКМ &#18dbd1- убрать в истёкшие"),
        @SerializedName("not_auction_owner")
        val notAuctionOwner: StringDesc.Raw = PREFIX
            .plus("&#f55442Вы не владелец этого слота")
            .toRaw(),
        @SerializedName("auction_deleted")
        val auctionDeleted: StringDesc.Raw = PREFIX
            .plus("&#f55442Слот удалён")
            .toRaw(),
        @SerializedName("auction_expired_notify")
        private val notifyAuctionExpired: StringDesc.Raw = PREFIX
            .plus("&#f55442Ваш слот %item% за %price% только что был просрочен")
            .toRaw(),
        @SerializedName("max_slots")
        val maxAuctions: StringDesc.Raw = PREFIX
            .plus("&#f55442У вас уже макстимальное число лотов")
            .toRaw(),
        @SerializedName("auction_by")
        private val auctionBy: StringDesc.Raw = PREFIX
            .plus("&7Выставил: &#d6a213%player_owner%")
            .toRaw(),
        @SerializedName("auction_created_ago")
        private val auctionCreatedAgo: StringDesc.Raw = StringDesc.Raw("&7Время: &#d6a213%time%"),
        @SerializedName("auction_last")
        private val auctionLast: StringDesc.Raw = StringDesc.Raw("&7Последний слот: &#d6a213%time%"),
        @SerializedName("auction_price")
        private val auctionPrice: StringDesc.Raw = StringDesc.Raw("&7Стоимость: &#d6a213%price%"),
        @SerializedName("sort.asc")
        val sortAscSymbol: StringDesc.Raw = StringDesc.Raw(" &6&l↓"),
        @SerializedName("sort.desc")
        val sortDescSymbol: StringDesc.Raw = StringDesc.Raw(" &6&l↑"),
        @SerializedName("sort.material_desc")
        val sortMaterial: StringDesc.Raw = StringDesc.Raw("• По материалу"),
        @SerializedName("sort.date_desc")
        val sortDate: StringDesc.Raw = StringDesc.Raw("• По дате"),
        @SerializedName("sort.name_desc")
        val sortName: StringDesc.Raw = StringDesc.Raw("• По имени"),
        @SerializedName("sort.amount_desc")
        val sortAmount: StringDesc.Raw = StringDesc.Raw("• По количеству"),
        @SerializedName("sort.price_desc")
        val sortPrice: StringDesc.Raw = StringDesc.Raw("• По цене"),
        @SerializedName("sort.player_asc")
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
        @SerializedName("reload_started")
        val reloadStarted: StringDesc.Raw = PREFIX
            .plus("&#dbbb18Перезагрузка плагина")
            .toRaw(),
        @SerializedName("reload_complete")
        val reloadSuccess: StringDesc.Raw = PREFIX
            .plus("&#42f596Перезагрузка успешно завершена")
            .toRaw(),
        @SerializedName("no_permission")
        val noPermissions: StringDesc.Raw = PREFIX
            .plus("&#f55442У вас нет прав")
            .toRaw(),
        @SerializedName("player_command")
        val onlyForPlayers: StringDesc.Raw = PREFIX
            .plus("&#f55442Эта команда только для игроков")
            .toRaw(),
        @SerializedName("wrong_args")
        val wrongArgs: StringDesc.Raw = PREFIX
            .plus("&#f55442Неверное использование команды")
            .toRaw(),
        @SerializedName("db_error")
        val dbError: StringDesc.Raw = PREFIX
            .plus("&#f55442Произошла ошибка")
            .toRaw(),
        @SerializedName("error")
        val unexpectedError: StringDesc.Raw = PREFIX
            .plus("&#f55442Произошла непредвиденная ошибка")
            .toRaw(),
        @SerializedName("time_format")
        val timeAgoFormat: StringDesc.Raw = StringDesc.Raw("%days%дн. %hours%ч. %minutes%м. назад")
    )

    companion object {
        private val PREFIX = StringDesc.Raw("&7[&#DBB72MARKET&7] ")
    }
}

private fun StringDesc.toRaw(): StringDesc.Raw {
    return StringDesc.Raw(this.raw)
}
