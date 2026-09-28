@file:Suppress("MaxLineLength", "LongParameterList")

package ru.astrainteractive.astramarket.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.PlaceholderReplacement
import ru.astrainteractive.astralibs.localization.component.replace
import ru.astrainteractive.astralibs.localization.component.replaceAll
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import ru.astrainteractive.astralibs.localization.text.LocalizedText

/**
 * Texts of the plugin, grouped by the feature that sends them. Every text has a default, so the plugin works
 * without `translations.yml` and a missing key keeps its default.
 */
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
        val title: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&6Market")
            translation(MinecraftLocales.RU_RU, "&6Рынок")
        },
        @SerialName("previous_page")
        val previousPage: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&6Previous")
            translation(MinecraftLocales.RU_RU, "&6Раньше")
        },
        @SerialName("next_page")
        val nextPage: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&6Next")
            translation(MinecraftLocales.RU_RU, "&6Дальше")
        },
        @SerialName("back")
        val back: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&6Back")
            translation(MinecraftLocales.RU_RU, "&6Назад")
        },
        /** Joined in front of the option a button currently shows. */
        @SerialName("selected_color")
        val selectedColor: LocalizedText = LocalizedText.shared("&6"),
        /** Joined in front of the options a button does not show. */
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
        /** The button that switches between expired and active lots. */
        @Serializable
        data class Filter(
            @SerialName("title")
            val title: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&6Freshness filter")
                translation(MinecraftLocales.RU_RU, "&6Фильтр новизны")
            },
            @SerialName("expired")
            val expired: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• Expired")
                translation(MinecraftLocales.RU_RU, "• Истекшие")
            },
            @SerialName("active")
            val active: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• New")
                translation(MinecraftLocales.RU_RU, "• Новые")
            }
        )

        /** The button that switches between lots and the players who sell them. */
        @Serializable
        data class Display(
            @SerialName("title")
            val title: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&6Display type")
                translation(MinecraftLocales.RU_RU, "&6Тип отображения")
            },
            @SerialName("by_player")
            val byPlayer: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By player")
                translation(MinecraftLocales.RU_RU, "• По игрокам")
            },
            @SerialName("by_item")
            val byItem: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By item")
                translation(MinecraftLocales.RU_RU, "• По предметам")
            }
        )

        @Serializable
        data class Sort(
            @SerialName("title")
            val title: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&6Sorting")
                translation(MinecraftLocales.RU_RU, "&6Сортировка")
            },
            @SerialName("ascending_arrow")
            val ascendingArrow: LocalizedText = LocalizedText.shared(" &6&l↓"),
            @SerialName("descending_arrow")
            val descendingArrow: LocalizedText = LocalizedText.shared(" &6&l↑"),
            @SerialName("by_material")
            val byMaterial: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By material")
                translation(MinecraftLocales.RU_RU, "• По материалу")
            },
            @SerialName("by_date")
            val byDate: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By date")
                translation(MinecraftLocales.RU_RU, "• По дате")
            },
            @SerialName("by_name")
            val byName: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By name")
                translation(MinecraftLocales.RU_RU, "• По имени")
            },
            @SerialName("by_amount")
            val byAmount: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By amount")
                translation(MinecraftLocales.RU_RU, "• По количеству")
            },
            @SerialName("by_price")
            val byPrice: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By price")
                translation(MinecraftLocales.RU_RU, "• По цене")
            },
            @SerialName("by_player")
            val byPlayer: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "• By player")
                translation(MinecraftLocales.RU_RU, "• По игроку")
            }
        )

        /** Lore of a lot in the menu. */
        @Serializable
        data class Lot(
            @SerialName("buy_hint")
            val buyHint: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#d6a213LMB &#18dbd1- buy")
                translation(MinecraftLocales.RU_RU, "&#d6a213ЛКМ &#18dbd1- купить")
            },
            @SerialName("remove_hint")
            val removeHint: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#d6a213RMB &#18dbd1- remove")
                translation(MinecraftLocales.RU_RU, "&#d6a213ПКМ &#18dbd1- убрать")
            },
            @SerialName("expire_hint")
            val expireHint: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#d6a213MMB &#18dbd1- move to expired")
                translation(MinecraftLocales.RU_RU, "&#d6a213СКМ &#18dbd1- убрать в истёкшие")
            },
            @SerialName("seller")
            private val seller: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&7Seller: &#d6a213%player_owner%")
                translation(MinecraftLocales.RU_RU, "&7Выставил: &#d6a213%player_owner%")
            },
            @SerialName("listed")
            private val listed: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&7Listed: &#d6a213%time%")
                translation(MinecraftLocales.RU_RU, "&7Время: &#d6a213%time%")
            },
            @SerialName("price")
            private val price: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&7Price: &#d6a213%price%")
                translation(MinecraftLocales.RU_RU, "&7Стоимость: &#d6a213%price%")
            }
        ) {
            fun seller(playerName: String): LocalizableComponent = seller.replace("%player_owner%", playerName)

            fun listed(timeAgo: LocalizableComponent): LocalizableComponent = listed.replace("%time%", timeAgo)

            fun price(price: Number): LocalizableComponent = this.price.replace("%price%", "$price")
        }

        /** Lore of a player's head in the menu of players who sell. */
        @Serializable
        data class Player(
            @SerialName("lots")
            private val lots: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&7Lots: %amount%")
                translation(MinecraftLocales.RU_RU, "&7Количество: %amount%")
            },
            @SerialName("latest_lot")
            private val latestLot: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&7Latest lot: &#d6a213%time%")
                translation(MinecraftLocales.RU_RU, "&7Последний слот: &#d6a213%time%")
            }
        ) {
            fun lots(amount: Int): LocalizableComponent = lots.replace("%amount%", "$amount")

            fun latestLot(timeAgo: LocalizableComponent): LocalizableComponent = latestLot.replace("%time%", timeAgo)
        }

        /** How long ago a lot was listed; the largest unit that is not zero picks the format. */
        @Serializable
        data class TimeAgo(
            @SerialName("days_hours_minutes")
            val daysHoursMinutes: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%days%d %hours%h %minutes%m ago")
                translation(MinecraftLocales.RU_RU, "%days%дн. %hours%ч. %minutes%м. назад")
            },
            @SerialName("hours_minutes")
            val hoursMinutes: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%hours%h %minutes%m ago")
                translation(MinecraftLocales.RU_RU, "%hours%ч. %minutes%м. назад")
            },
            @SerialName("minutes")
            val minutes: LocalizedText = LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%minutes%m ago")
                translation(MinecraftLocales.RU_RU, "%minutes%м. назад")
            }
        )
    }

    @Serializable
    data class Sell(
        @SerialName("wrong_item")
        val wrongItem: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442The item in your hand can't be sold")
                translation(MinecraftLocales.RU_RU, "&#f55442Предмет в вашей руке не подходит для продажи")
            }
        ),
        @SerialName("too_many_lots")
        val tooManyLots: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442You already have the maximum number of lots")
                translation(MinecraftLocales.RU_RU, "&#f55442У вас уже максимальное число лотов")
            }
        ),
        @SerialName("wrong_price")
        val wrongPrice: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442The price is out of the allowed range")
                translation(MinecraftLocales.RU_RU, "&#f55442Неверный ценовой диапазон")
            }
        ),
        @SerialName("listed")
        val listed: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#18dbd1The item is now on the auction")
                translation(MinecraftLocales.RU_RU, "&#18dbd1Предмет добавлен на аукцион")
            }
        ),
        @SerialName("announcement")
        private val announcement: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#d6a213%player% &#18dbd1put a new item on /market")
                translation(
                    MinecraftLocales.RU_RU,
                    "&#18dbd1Игрок &#d6a213%player% &#18dbd1выставил на /market новый предмет"
                )
            }
        )
    ) {
        fun announcement(playerName: String): LocalizableComponent = announcement.replace("%player%", playerName)
    }

    /** Item names are set by players on an anvil, so they go into these texts as plain text and never become a click. */
    @Serializable
    data class Buy(
        @SerialName("own_lot")
        val ownLot: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442You can't buy your own lot")
                translation(MinecraftLocales.RU_RU, "&#f55442Вы не можете купить собственный лот")
            }
        ),
        @SerialName("not_enough_money")
        val notEnoughMoney: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442You don't have enough money")
                translation(MinecraftLocales.RU_RU, "&#f55442У вас недостаточно денег")
            }
        ),
        @SerialName("payment_failed")
        val paymentFailed: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442Could not pay out the money")
                translation(MinecraftLocales.RU_RU, "&#f55442Не удалось выплатить деньги")
            }
        ),
        @SerialName("bought")
        private val bought: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "&#18dbd1You bought &#dbaa18%item%&#18dbd1 from &#dbaa18%player_owner%&#18dbd1 for &#dbaa18%price%"
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "&#18dbd1Вы купили предмет &#dbaa18%item%&#18dbd1 у игрока &#dbaa18%player_owner%&#18dbd1 за &#dbaa18%price%"
                )
            }
        ),
        @SerialName("sold")
        private val sold: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "&#dbaa18%player%&#18dbd1 bought your &#dbaa18%item%&#18dbd1 for &#dbaa18%price%"
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "&#18dbd1Игрок &#dbaa18%player%&#18dbd1 купил у вас &#dbaa18%item%&#18dbd1 за &#dbaa18%price%"
                )
            }
        )
    ) {
        /** Sent to the buyer. */
        fun bought(sellerName: String, itemName: String, price: Number): LocalizableComponent {
            return bought.replaceAll(
                PlaceholderReplacement.plain("%item%", itemName),
                PlaceholderReplacement.plain("%player_owner%", sellerName),
                PlaceholderReplacement.plain("%price%", "$price")
            )
        }

        /** Sent to the seller. */
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
        val notOwner: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442You don't own this lot")
                translation(MinecraftLocales.RU_RU, "&#f55442Вы не владелец этого слота")
            }
        ),
        @SerialName("removed")
        val removed: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442The lot is removed")
                translation(MinecraftLocales.RU_RU, "&#f55442Слот удалён")
            }
        )
    )

    @Serializable
    data class Expire(
        /** Sent to the moderator who expired the lot. */
        @SerialName("success")
        val success: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#d6a213You expired the lot!")
                translation(MinecraftLocales.RU_RU, "&#d6a213Вы просрочили слот!")
            }
        ),
        @SerialName("owner_notice")
        private val ownerNotice: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442Your lot %item% for %price% has just expired")
                translation(MinecraftLocales.RU_RU, "&#f55442Ваш слот %item% за %price% только что был просрочен")
            }
        )
    ) {
        /** Sent to the owner of the lot; the item name goes in as plain text and never becomes a click. */
        fun ownerNotice(itemName: String, price: Number): LocalizableComponent = ownerNotice.replaceAll(
            PlaceholderReplacement.plain("%item%", itemName),
            PlaceholderReplacement.plain("%price%", "$price")
        )
    }

    @Serializable
    data class Reload(
        @SerialName("started")
        val started: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#dbbb18Reloading the plugin")
                translation(MinecraftLocales.RU_RU, "&#dbbb18Перезагрузка плагина")
            }
        ),
        @SerialName("completed")
        val completed: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596Reload complete")
                translation(MinecraftLocales.RU_RU, "&#42f596Перезагрузка успешно завершена")
            }
        )
    )

    /** Failures that several operations report. */
    @Serializable
    data class Error(
        @SerialName("no_permission")
        val noPermission: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442You don't have permission")
                translation(MinecraftLocales.RU_RU, "&#f55442У вас нет прав")
            }
        ),
        @SerialName("only_player_command")
        val onlyPlayerCommand: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442This command is for players only")
                translation(MinecraftLocales.RU_RU, "&#f55442Эта команда только для игроков")
            }
        ),
        @SerialName("wrong_usage")
        val wrongUsage: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442Wrong usage")
                translation(MinecraftLocales.RU_RU, "&#f55442Неверное использование")
            }
        ),
        @SerialName("inventory_full")
        val inventoryFull: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442Your inventory is full")
                translation(MinecraftLocales.RU_RU, "&#f55442Инвентарь полон")
            }
        ),
        @SerialName("database")
        val database: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442An error occurred")
                translation(MinecraftLocales.RU_RU, "&#f55442Произошла ошибка")
            }
        ),
        @SerialName("unexpected")
        val unexpected: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#f55442An unexpected error occurred")
                translation(MinecraftLocales.RU_RU, "&#f55442Произошла непредвиденная ошибка")
            }
        )
    )

    companion object {
        private val PREFIX = LocalizedText.shared("&7[&6MARKET&7] ")
    }
}
