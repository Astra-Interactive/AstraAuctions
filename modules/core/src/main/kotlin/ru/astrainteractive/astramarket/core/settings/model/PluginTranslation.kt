@file:Suppress("MaxLineLength", "LongParameterList", "DataClassContainsFunctions")

package ru.astrainteractive.astramarket.core.settings.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.PlaceholderReplacement
import ru.astrainteractive.astralibs.localization.component.replace
import ru.astrainteractive.astralibs.localization.component.replaceAll
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import ru.astrainteractive.astralibs.localization.text.LocalizedText

@Serializable
data class PluginTranslation(
    @SerialName("menu")
    val menu: Menu = Menu(),
    @SerialName("sell")
    val sell: Sell = Sell(),
    @SerialName("buy")
    val buy: Buy = Buy(),
    @SerialName("remove")
    val remove: Remove = Remove(),
    @SerialName("expire")
    val expire: Expire = Expire(),
    @SerialName("reload")
    val reload: Reload = Reload(),
    @SerialName("error")
    val error: Error = Error()
) {
    @Serializable
    data class Menu(
        @SerialName("title")
        val title: LocalizedText = localized(en = "&6Market", ru = "&6Рынок"),
        @SerialName("previous_page")
        val previousPage: LocalizedText = localized(en = "&6Previous", ru = "&6Раньше"),
        @SerialName("next_page")
        val nextPage: LocalizedText = localized(en = "&6Next", ru = "&6Дальше"),
        @SerialName("back")
        val back: LocalizedText = localized(en = "&6Back", ru = "&6Назад"),
        @SerialName("selected_color")
        val selectedColor: LocalizedText = LocalizedText.shared("&6"),
        @SerialName("unselected_color")
        val unselectedColor: LocalizedText = LocalizedText.shared("&f"),
        @SerialName("filter")
        val filter: Filter = Filter(),
        @SerialName("display")
        val display: Display = Display(),
        @SerialName("sort")
        val sort: Sort = Sort(),
        @SerialName("lot")
        val lot: Lot = Lot(),
        @SerialName("player")
        val player: Player = Player(),
        @SerialName("time_ago")
        val timeAgo: TimeAgo = TimeAgo()
    ) {
        @Serializable
        data class Filter(
            @SerialName("title")
            val title: LocalizedText = localized(en = "&6Freshness filter", ru = "&6Фильтр новизны"),
            @SerialName("expired")
            val expired: LocalizedText = localized(en = "• Expired", ru = "• Истекшие"),
            @SerialName("active")
            val active: LocalizedText = localized(en = "• New", ru = "• Новые")
        )

        @Serializable
        data class Display(
            @SerialName("title")
            val title: LocalizedText = localized(en = "&6Display type", ru = "&6Тип отображения"),
            @SerialName("by_player")
            val byPlayer: LocalizedText = localized(en = "• By player", ru = "• По игрокам"),
            @SerialName("by_item")
            val byItem: LocalizedText = localized(en = "• By item", ru = "• По предметам")
        )

        @Serializable
        data class Sort(
            @SerialName("title")
            val title: LocalizedText = localized(en = "&6Sorting", ru = "&6Сортировка"),
            @SerialName("ascending_arrow")
            val ascendingArrow: LocalizedText = LocalizedText.shared(" &6&l↓"),
            @SerialName("descending_arrow")
            val descendingArrow: LocalizedText = LocalizedText.shared(" &6&l↑"),
            @SerialName("by_material")
            val byMaterial: LocalizedText = localized(en = "• By material", ru = "• По материалу"),
            @SerialName("by_date")
            val byDate: LocalizedText = localized(en = "• By date", ru = "• По дате"),
            @SerialName("by_name")
            val byName: LocalizedText = localized(en = "• By name", ru = "• По имени"),
            @SerialName("by_amount")
            val byAmount: LocalizedText = localized(en = "• By amount", ru = "• По количеству"),
            @SerialName("by_price")
            val byPrice: LocalizedText = localized(en = "• By price", ru = "• По цене"),
            @SerialName("by_player")
            val byPlayer: LocalizedText = localized(en = "• By player", ru = "• По игроку")
        )

        @Serializable
        data class Lot(
            @SerialName("buy_hint")
            val buyHint: LocalizedText = localized(
                en = "&#d6a213LMB &#18dbd1- buy",
                ru = "&#d6a213ЛКМ &#18dbd1- купить"
            ),
            @SerialName("remove_hint")
            val removeHint: LocalizedText = localized(
                en = "&#d6a213RMB &#18dbd1- remove",
                ru = "&#d6a213ПКМ &#18dbd1- убрать"
            ),
            @SerialName("expire_hint")
            val expireHint: LocalizedText = localized(
                en = "&#d6a213MMB &#18dbd1- move to expired",
                ru = "&#d6a213СКМ &#18dbd1- убрать в истёкшие"
            ),
            @SerialName("seller")
            private val seller: LocalizedText = localized(
                en = "&7Seller: &#d6a213%player_owner%",
                ru = "&7Выставил: &#d6a213%player_owner%"
            ),
            @SerialName("listed")
            private val listed: LocalizedText = localized(
                en = "&7Listed: &#d6a213%time%",
                ru = "&7Время: &#d6a213%time%"
            ),
            @SerialName("price")
            private val price: LocalizedText = localized(
                en = "&7Price: &#d6a213%price%",
                ru = "&7Стоимость: &#d6a213%price%"
            )
        ) {
            fun seller(playerName: String): LocalizableComponent = seller.replace("%player_owner%", playerName)

            fun listed(timeAgo: LocalizableComponent): LocalizableComponent = listed.replace("%time%", timeAgo)

            fun price(price: Number): LocalizableComponent = this.price.replace("%price%", "$price")
        }

        @Serializable
        data class Player(
            @SerialName("lots")
            private val lots: LocalizedText = localized(en = "&7Lots: %amount%", ru = "&7Количество: %amount%"),
            @SerialName("latest_lot")
            private val latestLot: LocalizedText = localized(
                en = "&7Latest lot: &#d6a213%time%",
                ru = "&7Последний слот: &#d6a213%time%"
            )
        ) {
            fun lots(amount: Int): LocalizableComponent = lots.replace("%amount%", "$amount")

            fun latestLot(timeAgo: LocalizableComponent): LocalizableComponent = latestLot.replace("%time%", timeAgo)
        }

        @Serializable
        data class TimeAgo(
            @SerialName("days_hours_minutes")
            val daysHoursMinutes: LocalizedText = localized(
                en = "%days%d %hours%h %minutes%m ago",
                ru = "%days%дн. %hours%ч. %minutes%м. назад"
            ),
            @SerialName("hours_minutes")
            val hoursMinutes: LocalizedText = localized(
                en = "%hours%h %minutes%m ago",
                ru = "%hours%ч. %minutes%м. назад"
            ),
            @SerialName("minutes")
            val minutes: LocalizedText = localized(en = "%minutes%m ago", ru = "%minutes%м. назад")
        )
    }

    @Serializable
    data class Sell(
        @SerialName("wrong_item")
        val wrongItem: LocalizedText = prefixed(
            en = "&#f55442The item in your hand can't be sold",
            ru = "&#f55442Предмет в вашей руке не подходит для продажи"
        ),
        @SerialName("too_many_lots")
        val tooManyLots: LocalizedText = prefixed(
            en = "&#f55442You already have the maximum number of lots",
            ru = "&#f55442У вас уже максимальное число лотов"
        ),
        @SerialName("wrong_price")
        val wrongPrice: LocalizedText = prefixed(
            en = "&#f55442The price is out of the allowed range",
            ru = "&#f55442Неверный ценовой диапазон"
        ),
        @SerialName("listed")
        val listed: LocalizedText = prefixed(
            en = "&#18dbd1The item is now on the auction",
            ru = "&#18dbd1Предмет добавлен на аукцион"
        ),
        @SerialName("announcement")
        private val announcement: LocalizedText = prefixed(
            en = "&#d6a213%player% &#18dbd1put a new item on /market",
            ru = "&#18dbd1Игрок &#d6a213%player% &#18dbd1выставил на /market новый предмет"
        )
    ) {
        fun announcement(playerName: String): LocalizableComponent = announcement.replace("%player%", playerName)
    }

    @Serializable
    data class Buy(
        @SerialName("own_lot")
        val ownLot: LocalizedText = prefixed(
            en = "&#f55442You can't buy your own lot",
            ru = "&#f55442Вы не можете купить собственный лот"
        ),
        @SerialName("not_enough_money")
        val notEnoughMoney: LocalizedText = prefixed(
            en = "&#f55442You don't have enough money",
            ru = "&#f55442У вас недостаточно денег"
        ),
        @SerialName("payment_failed")
        val paymentFailed: LocalizedText = prefixed(
            en = "&#f55442Could not pay out the money",
            ru = "&#f55442Не удалось выплатить деньги"
        ),
        @SerialName("bought")
        private val bought: LocalizedText = prefixed(
            en = "&#18dbd1You bought &#dbaa18%item%&#18dbd1 from &#dbaa18%player_owner%&#18dbd1 for &#dbaa18%price%",
            ru = "&#18dbd1Вы купили предмет &#dbaa18%item%&#18dbd1 у игрока &#dbaa18%player_owner%&#18dbd1 за &#dbaa18%price%"
        ),
        @SerialName("sold")
        private val sold: LocalizedText = prefixed(
            en = "&#dbaa18%player%&#18dbd1 bought your &#dbaa18%item%&#18dbd1 for &#dbaa18%price%",
            ru = "&#18dbd1Игрок &#dbaa18%player%&#18dbd1 купил у вас &#dbaa18%item%&#18dbd1 за &#dbaa18%price%"
        )
    ) {
        fun bought(sellerName: String, itemName: String, price: Number): LocalizableComponent {
            return bought.replaceAll(
                PlaceholderReplacement.plain("%item%", itemName),
                PlaceholderReplacement.plain("%player_owner%", sellerName),
                PlaceholderReplacement.plain("%price%", "$price")
            )
        }

        fun sold(buyerName: String, itemName: String, price: Number): LocalizableComponent {
            return sold.replaceAll(
                PlaceholderReplacement.plain("%player%", buyerName),
                PlaceholderReplacement.plain("%item%", itemName),
                PlaceholderReplacement.plain("%price%", "$price")
            )
        }
    }

    @Serializable
    data class Remove(
        @SerialName("not_owner")
        val notOwner: LocalizedText = prefixed(
            en = "&#f55442You don't own this lot",
            ru = "&#f55442Вы не владелец этого слота"
        ),
        @SerialName("removed")
        val removed: LocalizedText = prefixed(
            en = "&#f55442The lot is removed",
            ru = "&#f55442Слот удалён"
        )
    )

    @Serializable
    data class Expire(
        @SerialName("success")
        val success: LocalizedText = prefixed(
            en = "&#d6a213You expired the lot!",
            ru = "&#d6a213Вы просрочили слот!"
        ),
        @SerialName("owner_notice")
        private val ownerNotice: LocalizedText = prefixed(
            en = "&#f55442Your lot %item% for %price% has just expired",
            ru = "&#f55442Ваш слот %item% за %price% только что был просрочен"
        )
    ) {
        fun ownerNotice(itemName: String, price: Number): LocalizableComponent {
            return ownerNotice.replaceAll(
                PlaceholderReplacement.plain("%item%", itemName),
                PlaceholderReplacement.plain("%price%", "$price")
            )
        }
    }

    @Serializable
    data class Reload(
        @SerialName("started")
        val started: LocalizedText = prefixed(
            en = "&#dbbb18Reloading the plugin",
            ru = "&#dbbb18Перезагрузка плагина"
        ),
        @SerialName("completed")
        val completed: LocalizedText = prefixed(
            en = "&#42f596Reload complete",
            ru = "&#42f596Перезагрузка успешно завершена"
        )
    )

    @Serializable
    data class Error(
        @SerialName("no_permission")
        val noPermission: LocalizedText = prefixed(
            en = "&#f55442You don't have permission",
            ru = "&#f55442У вас нет прав"
        ),
        @SerialName("only_player_command")
        val onlyPlayerCommand: LocalizedText = prefixed(
            en = "&#f55442This command is for players only",
            ru = "&#f55442Эта команда только для игроков"
        ),
        @SerialName("wrong_usage")
        val wrongUsage: LocalizedText = prefixed(
            en = "&#f55442Wrong usage",
            ru = "&#f55442Неверное использование"
        ),
        @SerialName("invalid_argument")
        val invalidArgument: LocalizedText = prefixed(
            en = "&#f55442Invalid argument value",
            ru = "&#f55442Неверное значение аргумента"
        ),
        @SerialName("player_not_found")
        val playerNotFound: LocalizedText = prefixed(
            en = "&#f55442Player not found",
            ru = "&#f55442Игрок не найден"
        ),
        @SerialName("inventory_full")
        val inventoryFull: LocalizedText = prefixed(
            en = "&#f55442Your inventory is full",
            ru = "&#f55442Инвентарь полон"
        ),
        @SerialName("database")
        val database: LocalizedText = prefixed(
            en = "&#f55442An error occurred",
            ru = "&#f55442Произошла ошибка"
        ),
        @SerialName("unexpected")
        val unexpected: LocalizedText = prefixed(
            en = "&#f55442An unexpected error occurred",
            ru = "&#f55442Произошла непредвиденная ошибка"
        )
    )

    companion object {
        private val PREFIX = LocalizedText.shared("&7[&6MARKET&7] ")

        private fun localized(en: String, ru: String): LocalizedText {
            return LocalizedText.build {
                translation(MinecraftLocales.EN_US, en)
                translation(MinecraftLocales.RU_RU, ru)
            }
        }

        private fun prefixed(en: String, ru: String): LocalizedText {
            return PREFIX.concat(localized(en, ru))
        }
    }
}
