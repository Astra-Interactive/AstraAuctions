package com.astrainteractive.astratemplate.utils

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.getHEXString

class Translation {
    companion object {
        lateinit var instance: Translation
    }

    init {
        instance = this
    }

    private val _translationFile: FileManager = FileManager("translations.yml")
    private val translation = _translationFile.getConfig()!!


    private fun getHEXString(path: String, default: String): String {
        if (!translation.contains(path)) {
            translation.set(path, default)
            _translationFile.saveConfig()
        }
        return translation.getHEXString(path) ?: default.HEX()
    }


    //General
    val reloadStarted: String = getHEXString("general.reload_started", "#dbbb18Перезагрузка плагина")
    val reloadSuccess: String =
        getHEXString("general.reload_complete", "#42f596Перезагрузка успешно завершена")
    val noPermissions: String =
        getHEXString("general.no_permission", "#f55442У вас нет прав")
    val onlyForPlayers: String =
        getHEXString("general.player_command", "#f55442Эта команда только для игроков")
    val wrongArgs: String =
        getHEXString("general.wrong_args", "#f55442Неверное использование команды")
    val dbError: String =
        getHEXString("general.db_error", "#f55442Произошла ошибка")
    val unexpectedError: String =
        getHEXString("general.error", "#f55442Произошла непредвиденная ошибка")

    //Auction
    val wrongItemInHand: String =
        getHEXString("auction.wrong_item", "#f55442Предмет в вашей руке не подходит для продажи")
    val wrongPrice: String =
        getHEXString("auction.wrong_price", "#f55442Неверный ценовой диапазон")
    val auctionAdded: String =
        getHEXString("auction.slot_added", "#18dbd1Предмет добавлен на аукцион")
    val inventoryFull: String =
        getHEXString("auction.inventory_full", "#f55442Инвентарь полон")
    val ownerCantBeBuyer: String =
        getHEXString("auction.owner_not_buyer", "#f55442Вы не можете купить собственный лот")
    val failedToPay: String =
        getHEXString("auction.failed_to_pay", "#f55442Не удалось выплатить деньги")
    val notifyUserBuy: String =
        getHEXString("auction.notifyUserBuy", "#18dbd1Вы купили предмет #dbaa18%item%#18dbd1 у игрока #dbaa18%player_owner%#18dbd1 за #dbaa18%price%")
    val notifyOwnerUserBuy: String =
        getHEXString("auction.notifyOwnerUserBuy", "#18dbd1Игрок #dbaa18%player%#18dbd1 купил у вас #dbaa18%item%#18dbd1 за #dbaa18%price%")
    val notEnoughMoney: String =
        getHEXString("auction.not_enough_money", "#f55442У вас недостаточно денег")
    val broadcast: String =
        getHEXString("auction.broadcast", "#18dbd1Игрок #dbaa18%player% #18dbd1выставил на /aauc новый предмет")
    val leftButton: String =
        getHEXString("menu.left_button", "#d6a213ЛКМ #18dbd1- купить")
    val rightButton: String =
        getHEXString("menu.right_button", "#d6a213ПКМ #18dbd1- убрать")
    val notAuctionOwner: String =
        getHEXString("menu.not_auction_owner", "#f55442Вы не владелец этого слота")
    val auctionDeleted: String =
        getHEXString("menu.auction_deleted", "#f55442Слот удалён")

    //Menu
    val title: String =
        getHEXString("menu.title", "#1382d6Аукцион")
    val back: String =
        getHEXString("menu.back", "#18dbd1Назад")
    val prev: String =
        getHEXString("menu.prev", "#18dbd1Раньше")
    val next: String =
        getHEXString("menu.next", "#18dbd1Дальше")
    val sort: String =
        getHEXString("menu.sort", "#18dbd1Сортировка")
    val maxAuctions: String =
        getHEXString("auction.max_slots", "#f55442У вас уже макстимальное число лотов")

    val auctionBy: String =
        getHEXString("auction.auction_by", "&7Выставил: #d6a213%player_owner%")
    val auctionCreatedAgo: String =
        getHEXString("auction.auction_created_ago", "&7Время: #d6a213%time%")
    val auctionPrice: String =
        getHEXString("auction.auction_price", "&7Стоимость: #d6a213%price%")

    val sortMaterialDesc: String =
        getHEXString("auction.sort.material_desc", "#FFFFFFпо материалу #d6a213&l↑")
    val sortMaterialAsc: String =
        getHEXString("auction.sort.material_asc", "#FFFFFFпо материалу #d6a213&l↓")

    val sortDateDesc: String =
        getHEXString("auction.sort.date_desc", "#FFFFFFпо дате #d6a213&l↑")
    val sortDateAsc: String =
        getHEXString("auction.sort.date_asc", "#FFFFFFпо дате #d6a213&l↓")

    val sortNameDesc: String =
        getHEXString("auction.sort.name_desc", "#FFFFFFпо имени #d6a213&l↑")
    val sortNameAsc: String =
        getHEXString("auction.sort.name_asc", "#FFFFFFпо имени #d6a213&l↓")

    val sortPriceDesc: String =
        getHEXString("auction.sort.price_desc", "#FFFFFFпо цене #d6a213&l↑")
    val sortPriceAsc: String =
        getHEXString("auction.sort.price_asc", "#FFFFFFпо цене #d6a213&l↓")

    val sortPlayerDesc: String =
        getHEXString("auction.sort.player_desc", "#FFFFFFпо игроку #d6a213&l↑")
    val sortPlayerAsc: String =
        getHEXString("auction.sort.player_asc", "#FFFFFFпо игроку #d6a213&l↓")


}