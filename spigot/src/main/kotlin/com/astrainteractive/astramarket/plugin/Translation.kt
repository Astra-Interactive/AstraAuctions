package com.astrainteractive.astramarket.plugin

import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astralibs.utils.BaseTranslation

class Translation() : BaseTranslation() {

    override val translationFile: FileManager = FileManager("translations.yml")


    //General
    val reloadStarted: String = translationValue("general.reload_started", "#dbbb18Перезагрузка плагина")
    val reloadSuccess: String =
        translationValue("general.reload_complete", "#42f596Перезагрузка успешно завершена")
    val noPermissions: String =
        translationValue("general.no_permission", "#f55442У вас нет прав")
    val onlyForPlayers: String =
        translationValue("general.player_command", "#f55442Эта команда только для игроков")
    val wrongArgs: String =
        translationValue("general.wrong_args", "#f55442Неверное использование команды")
    val dbError: String =
        translationValue("general.db_error", "#f55442Произошла ошибка")
    val unexpectedError: String =
        translationValue("general.error", "#f55442Произошла непредвиденная ошибка")
    val timeAgoFormat: String =
        translationValue("general.time_format", "%days%дн. %hours%ч. %minutes%м. назад")

    //Auction
    val wrongItemInHand: String =
        translationValue("auction.wrong_item", "#f55442Предмет в вашей руке не подходит для продажи")
    val wrongPrice: String =
        translationValue("auction.wrong_price", "#f55442Неверный ценовой диапазон")
    val auctionAdded: String =
        translationValue("auction.slot_added", "#18dbd1Предмет добавлен на аукцион")
    val inventoryFull: String =
        translationValue("auction.inventory_full", "#f55442Инвентарь полон")
    val ownerCantBeBuyer: String =
        translationValue("auction.owner_not_buyer", "#f55442Вы не можете купить собственный лот")
    val failedToPay: String =
        translationValue("auction.failed_to_pay", "#f55442Не удалось выплатить деньги")
    val notifyUserBuy: String =
        translationValue(
            "auction.notifyUserBuy",
            "#18dbd1Вы купили предмет #dbaa18%item%#18dbd1 у игрока #dbaa18%player_owner%#18dbd1 за #dbaa18%price%"
        )
    val notifyOwnerUserBuy: String =
        translationValue(
            "auction.notifyOwnerUserBuy",
            "#18dbd1Игрок #dbaa18%player%#18dbd1 купил у вас #dbaa18%item%#18dbd1 за #dbaa18%price%"
        )
    val notEnoughMoney: String =
        translationValue("auction.not_enough_money", "#f55442У вас недостаточно денег")
    val broadcast: String =
        translationValue("auction.broadcast", "#18dbd1Игрок #d6a213%player% #18dbd1выставил на /aauc новый предмет")
    val tabCompleterPrice: String =
        translationValue("auction.tab_completer.price", "ЦЕНА")
    val tabCompleterAmount: String =
        translationValue("auction.tab_completer.amount", "КОЛИЧЕСТВО")
    val auctionHasBeenExpired: String =
        translationValue("auction.auction_been_expired", "#d6a213Вы просрочили слот!")
    val leftButton: String =
        translationValue("menu.left_button", "#d6a213ЛКМ #18dbd1- купить")
    val rightButton: String =
        translationValue("menu.right_button", "#d6a213ПКМ #18dbd1- убрать")
    val middleClick: String =
        translationValue("menu.middle_click", "#d6a213СКМ #18dbd1- убрать в истёкшие")
    val notAuctionOwner: String =
        translationValue("menu.not_auction_owner", "#f55442Вы не владелец этого слота")
    val auctionDeleted: String =
        translationValue("menu.auction_deleted", "#f55442Слот удалён")
    val notifyAuctionExpired: String =
        translationValue("menu.auction_expired_notify", "#f55442Ваш слот %item% за %price% только что был просрочен")

    //Menu
    val title: String =
        translationValue("menu.title", "#1382d6Аукцион")
    val expiredTitle: String =
        translationValue("menu.title_expired", "#1382d6Истекшие")
    val back: String =
        translationValue("menu.back", "#18dbd1Назад")
    val prev: String =
        translationValue("menu.prev", "#18dbd1Раньше")
    val aauc: String =
        translationValue("menu.aauc", "#18dbd1Аукцион")
    val expired: String =
        translationValue("menu.expired", "#18dbd1Истекшие")
    val next: String =
        translationValue("menu.next", "#18dbd1Дальше")
    val sort: String =
        translationValue("menu.sort", "#18dbd1Сортировка")
    val maxAuctions: String =
        translationValue("auction.max_slots", "#f55442У вас уже макстимальное число лотов")

    val auctionBy: String =
        translationValue("auction.auction_by", "&7Выставил: #d6a213%player_owner%")
    val auctionCreatedAgo: String =
        translationValue("auction.auction_created_ago", "&7Время: #d6a213%time%")
    val auctionPrice: String =
        translationValue("auction.auction_price", "&7Стоимость: #d6a213%price%")

    val sortMaterialDesc: String =
        translationValue("auction.sort.material_desc", "#FFFFFFпо материалу #d6a213&l↑")
    val sortMaterialAsc: String =
        translationValue("auction.sort.material_asc", "#FFFFFFпо материалу #d6a213&l↓")

    val sortDateDesc: String =
        translationValue("auction.sort.date_desc", "#FFFFFFпо дате #d6a213&l↑")
    val sortDateAsc: String =
        translationValue("auction.sort.date_asc", "#FFFFFFпо дате #d6a213&l↓")

    val sortNameDesc: String =
        translationValue("auction.sort.name_desc", "#FFFFFFпо имени #d6a213&l↑")
    val sortNameAsc: String =
        translationValue("auction.sort.name_asc", "#FFFFFFпо имени #d6a213&l↓")

    val sortPriceDesc: String =
        translationValue("auction.sort.price_desc", "#FFFFFFпо цене #d6a213&l↑")
    val sortPriceAsc: String =
        translationValue("auction.sort.price_asc", "#FFFFFFпо цене #d6a213&l↓")

    val sortPlayerDesc: String =
        translationValue("auction.sort.player_desc", "#FFFFFFпо игроку #d6a213&l↑")
    val sortPlayerAsc: String =
        translationValue("auction.sort.player_asc", "#FFFFFFпо игроку #d6a213&l↓")


}