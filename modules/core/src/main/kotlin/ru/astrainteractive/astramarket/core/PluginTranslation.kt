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
        val enabledColor: LocalizedText = LocalizedText.shared("&6"),
        @SerialName("disabled_color")
        val disabledColor: LocalizedText = LocalizedText.shared("&f"),
        @SerialName("market")
        val market: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&6Рынок")
            translation(MinecraftLocales.EN_US, "&6Market")
        },
        @SerialName("filter")
        val filterExpired: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&6Фильтр новизны")
            translation(MinecraftLocales.EN_US, "&6Freshness filter")
        },
        @SerialName("expired")
        val expired: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• Истекшие")
            translation(MinecraftLocales.EN_US, "• Expired")
        },
        @SerialName("new")
        val new: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• Новые")
            translation(MinecraftLocales.EN_US, "• New")
        },
        @SerialName("next")
        val next: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&6Дальше")
            translation(MinecraftLocales.EN_US, "&6Next")
        },
        @SerialName("back")
        val back: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&6Назад")
            translation(MinecraftLocales.EN_US, "&6Back")
        },
        @SerialName("prev")
        val prev: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&6Раньше")
            translation(MinecraftLocales.EN_US, "&6Previous")
        },
        @SerialName("sort")
        val sort: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&6Сортировка")
            translation(MinecraftLocales.EN_US, "&6Sorting")
        },
        @SerialName("slots_filter")
        val displayType: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&6Тип отображения")
            translation(MinecraftLocales.EN_US, "&6Display type")
        },
        @SerialName("player_slots")
        val playerSlots: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По игрокам")
            translation(MinecraftLocales.EN_US, "• By player")
        },
        @SerialName("all_slots")
        val allSlots: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По предметам")
            translation(MinecraftLocales.EN_US, "• By item")
        },
    )

    @Serializable
    class Auction(
        @SerialName("wrong_item")
        val wrongItemInHand: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Предмет в вашей руке не подходит для продажи")
                translation(MinecraftLocales.EN_US, "&#f55442The item in your hand can't be sold")
            }
        ),
        @SerialName("wrong_price")
        val wrongPrice: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Неверный ценовой диапазон")
                translation(MinecraftLocales.EN_US, "&#f55442The price is out of the allowed range")
            }
        ),
        @SerialName("slot_added")
        val auctionAdded: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#18dbd1Предмет добавлен на аукцион")
                translation(MinecraftLocales.EN_US, "&#18dbd1The item is now on the auction")
            }
        ),
        @SerialName("inventory_full")
        val inventoryFull: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Инвентарь полон")
                translation(MinecraftLocales.EN_US, "&#f55442Your inventory is full")
            }
        ),
        @SerialName("owner_not_buyer")
        val ownerCantBeBuyer: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Вы не можете купить собственный лот")
                translation(MinecraftLocales.EN_US, "&#f55442You can't buy your own lot")
            }
        ),
        @SerialName("failed_to_pay")
        val failedToPay: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Не удалось выплатить деньги")
                translation(MinecraftLocales.EN_US, "&#f55442Could not pay out the money")
            }
        ),
        @SerialName("notifyUserBuy")
        private val notifyUserBuy: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "&#18dbd1Вы купили предмет &#dbaa18%item%&#18dbd1 у игрока &#dbaa18%player_owner%&#18dbd1 за &#dbaa18%price%"
                )
                translation(
                    MinecraftLocales.EN_US,
                    "&#18dbd1You bought &#dbaa18%item%&#18dbd1 from &#dbaa18%player_owner%&#18dbd1 for &#dbaa18%price%"
                )
            }
        ),
        @SerialName("notifyOwnerUserBuy")
        private val notifyOwnerUserBuy: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "&#18dbd1Игрок &#dbaa18%player%&#18dbd1 купил у вас &#dbaa18%item%&#18dbd1 за &#dbaa18%price%"
                )
                translation(
                    MinecraftLocales.EN_US,
                    "&#dbaa18%player%&#18dbd1 bought your &#dbaa18%item%&#18dbd1 for &#dbaa18%price%"
                )
            }
        ),
        @SerialName("not_enough_money")
        val notEnoughMoney: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442У вас недостаточно денег")
                translation(MinecraftLocales.EN_US, "&#f55442You don't have enough money")
            }
        ),
        @SerialName("broadcast")
        private val broadcast: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "&#18dbd1Игрок &#d6a213%player% &#18dbd1выставил на /market новый предмет"
                )
                translation(MinecraftLocales.EN_US, "&#d6a213%player% &#18dbd1put a new item on /market")
            }
        ),
        @SerialName("player.auctions_amount")
        private val auctionsAmount: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Количество: %amount%")
            translation(MinecraftLocales.EN_US, "&7Lots: %amount%")
        },
        @SerialName("tab_completer.price")
        val tabCompleterPrice: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "ЦЕНА")
            translation(MinecraftLocales.EN_US, "PRICE")
        },
        @SerialName("tab_completer.amount")
        val tabCompleterAmount: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "КОЛИЧЕСТВО")
            translation(MinecraftLocales.EN_US, "AMOUNT")
        },
        @SerialName("auction_been_expired")
        val auctionHasBeenExpired: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#d6a213Вы просрочили слот!")
                translation(MinecraftLocales.EN_US, "&#d6a213You expired the lot!")
            }
        ),
        @SerialName("left_button")
        val buySlot: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&#d6a213ЛКМ &#18dbd1- купить")
            translation(MinecraftLocales.EN_US, "&#d6a213LMB &#18dbd1- buy")
        },
        @SerialName("right_button")
        val removeSlot: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&#d6a213ПКМ &#18dbd1- убрать")
            translation(MinecraftLocales.EN_US, "&#d6a213RMB &#18dbd1- remove")
        },
        @SerialName("middle_click")
        val expireSlot: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&#d6a213СКМ &#18dbd1- убрать в истёкшие")
            translation(MinecraftLocales.EN_US, "&#d6a213MMB &#18dbd1- move to expired")
        },
        @SerialName("not_auction_owner")
        val notAuctionOwner: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Вы не владелец этого слота")
                translation(MinecraftLocales.EN_US, "&#f55442You don't own this lot")
            }
        ),
        @SerialName("auction_deleted")
        val auctionDeleted: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Слот удалён")
                translation(MinecraftLocales.EN_US, "&#f55442The lot is removed")
            }
        ),
        @SerialName("auction_expired_notify")
        private val notifyAuctionExpired: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Ваш слот %item% за %price% только что был просрочен")
                translation(MinecraftLocales.EN_US, "&#f55442Your lot %item% for %price% has just expired")
            }
        ),
        @SerialName("max_slots")
        val maxAuctions: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442У вас уже макстимальное число лотов")
                translation(MinecraftLocales.EN_US, "&#f55442You already have the maximum number of lots")
            }
        ),
        @SerialName("auction_by")
        private val auctionBy: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Выставил: &#d6a213%player_owner%")
            translation(MinecraftLocales.EN_US, "&7Seller: &#d6a213%player_owner%")
        },
        @SerialName("auction_created_ago")
        private val auctionCreatedAgo: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Время: &#d6a213%time%")
            translation(MinecraftLocales.EN_US, "&7Listed: &#d6a213%time%")
        },
        @SerialName("auction_last")
        private val auctionLast: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Последний слот: &#d6a213%time%")
            translation(MinecraftLocales.EN_US, "&7Latest lot: &#d6a213%time%")
        },
        @SerialName("auction_price")
        private val auctionPrice: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Стоимость: &#d6a213%price%")
            translation(MinecraftLocales.EN_US, "&7Price: &#d6a213%price%")
        },
        @SerialName("sort.asc")
        val sortAscSymbol: LocalizedText = LocalizedText.shared(" &6&l↓"),
        @SerialName("sort.desc")
        val sortDescSymbol: LocalizedText = LocalizedText.shared(" &6&l↑"),
        @SerialName("sort.material_desc")
        val sortMaterial: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По материалу")
            translation(MinecraftLocales.EN_US, "• By material")
        },
        @SerialName("sort.date_desc")
        val sortDate: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По дате")
            translation(MinecraftLocales.EN_US, "• By date")
        },
        @SerialName("sort.name_desc")
        val sortName: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По имени")
            translation(MinecraftLocales.EN_US, "• By name")
        },
        @SerialName("sort.amount_desc")
        val sortAmount: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По количеству")
            translation(MinecraftLocales.EN_US, "• By amount")
        },
        @SerialName("sort.price_desc")
        val sortPrice: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По цене")
            translation(MinecraftLocales.EN_US, "• By price")
        },
        @SerialName("sort.player_asc")
        val sortPlayer: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "• По игроку")
            translation(MinecraftLocales.EN_US, "• By player")
        },
    ) {
        fun auctionPrice(price: Number): LocalizableComponent = auctionPrice.replace("%price%", "$price")

        fun auctionLast(time: LocalizableComponent): LocalizableComponent = auctionLast.replace("%time%", time)

        fun auctionCreatedAgo(time: LocalizableComponent): LocalizableComponent {
            return auctionCreatedAgo.replace("%time%", time)
        }

        fun auctionBy(playerOwner: String): LocalizableComponent = auctionBy.replace("%player_owner%", playerOwner)

        /** Item names are set by players on an anvil, so they go in as plain text and never become a click. */
        fun notifyAuctionExpired(item: String, price: Number): LocalizableComponent = notifyAuctionExpired.replaceAll(
            PlaceholderReplacement.plain("%item%", item),
            PlaceholderReplacement.plain("%price%", "$price")
        )

        fun auctionsAmount(amount: Int): LocalizableComponent = auctionsAmount.replace("%amount%", "$amount")

        fun broadcast(playerName: String): LocalizableComponent = broadcast.replace("%player%", playerName)

        fun notifyOwnerUserBuy(playerName: String, itemName: String, price: Number): LocalizableComponent {
            return notifyOwnerUserBuy.replaceAll(
                PlaceholderReplacement.plain("%player%", playerName),
                PlaceholderReplacement.plain("%item%", itemName),
                PlaceholderReplacement.plain("%price%", "$price")
            )
        }

        fun notifyUserBuy(playerOwner: String, itemName: String, price: Number): LocalizableComponent {
            return notifyUserBuy.replaceAll(
                PlaceholderReplacement.plain("%item%", itemName),
                PlaceholderReplacement.plain("%player_owner%", playerOwner),
                PlaceholderReplacement.plain("%price%", "$price")
            )
        }
    }

    @Serializable
    class General(
        @SerialName("reload_started")
        val reloadStarted: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#dbbb18Перезагрузка плагина")
                translation(MinecraftLocales.EN_US, "&#dbbb18Reloading the plugin")
            }
        ),
        @SerialName("reload_complete")
        val reloadSuccess: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Перезагрузка успешно завершена")
                translation(MinecraftLocales.EN_US, "&#42f596Reload complete")
            }
        ),
        @SerialName("no_permission")
        val noPermissions: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442У вас нет прав")
                translation(MinecraftLocales.EN_US, "&#f55442You don't have permission")
            }
        ),
        @SerialName("player_command")
        val onlyForPlayers: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Эта команда только для игроков")
                translation(MinecraftLocales.EN_US, "&#f55442This command is for players only")
            }
        ),
        @SerialName("wrong_args")
        val wrongArgs: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Неверное использование команды")
                translation(MinecraftLocales.EN_US, "&#f55442Wrong command usage")
            }
        ),
        @SerialName("db_error")
        val dbError: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Произошла ошибка")
                translation(MinecraftLocales.EN_US, "&#f55442An error occurred")
            }
        ),
        @SerialName("error")
        val unexpectedError: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#f55442Произошла непредвиденная ошибка")
                translation(MinecraftLocales.EN_US, "&#f55442An unexpected error occurred")
            }
        ),
        @SerialName("time_format_dhm")
        val timeAgoFormatDHM: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "%days%дн. %hours%ч. %minutes%м. назад")
            translation(MinecraftLocales.EN_US, "%days%d %hours%h %minutes%m ago")
        },
        @SerialName("time_format_hm")
        val timeAgoFormatHM: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "%hours%ч. %minutes%м. назад")
            translation(MinecraftLocales.EN_US, "%hours%h %minutes%m ago")
        },
        @SerialName("time_format_m")
        val timeAgoFormatM: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "%minutes%м. назад")
            translation(MinecraftLocales.EN_US, "%minutes%m ago")
        }
    )

    companion object {
        private val PREFIX = LocalizedText.shared("&7[&6MARKET&7] ")
    }
}
