package com.astrainteractive.astratemplate.utils

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.getHEXString
import java.nio.file.Path

class Translation {
    companion object {
        lateinit var instanse: Translation
    }
    init {
        instanse = this
    }

    private val _translationFile: FileManager = FileManager("translations.yml")
    private val translation = _translationFile.getConfig()!!


    private fun getHEXString(path: String, default: String):String {
        if (!translation.contains(path)){
            translation.set(path,default)
            _translationFile.saveConfig()
        }
        return translation.getHEXString(path) ?: default.HEX()
    }


    //General
    val reloadStarted: String = getHEXString("general.reload_started","#dbbb18Перезагрузка плагина")
    val reloadSuccess: String =
        getHEXString("general.reload_complete","#42f596Перезагрузка успешно завершена")
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
    val itemBought: String =
        getHEXString("auction.item_bought", "#18dbd1Вы купили предмет")
    val notEnoughMoney: String =
        getHEXString("auction.not_enough_money", "#f55442У вас недостаточно денег")
    val broadcast: String =
        getHEXString("auction.broadcast", "#18dbd1Игрок %player% выставил на /aauc новый предмет")
    val leftButton: String =
        getHEXString("menu.left_button", "#d6a213ЛКМ #18dbd1- купить")
    val rightButton: String =
        getHEXString("menu.right_button", "#d6a213ПКМ #18dbd1- убрать")

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


}