package com.astrainteractive.astratemplate.utils

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.getHEXString
import java.nio.file.Path

/**
 * All translation stored here by default
 * It's okay to create another translations classes and files, but remember to create Translation class before others
 */
class Translation() {
    companion object {
        lateinit var instanse: Translation
    }

    init {
        instanse = this
    }

    /**
     * This is a default translation file. Don't forget to create translation.yml in resources of the plugin
     * translation contains non-null assert because translation.yml exist in every possible situation except whe you forget to add it to resources
     */
    val _translationFile: FileManager = FileManager("translations.yml")
    private val translation = _translationFile.getConfig()!!

    private fun getHEXString(path: String) = translation.getHEXString(path)
    private fun getHEXString(path: String, default: String) = translation.getHEXString(path) ?: default.HEX()


    val reloadStarted: String = getHEXString("RELOAD") ?: "#dbbb18Перезагрузка плагина".HEX()
    val reloadSuccess: String =
        getHEXString("RELOAD_COMPLETE") ?: "#42f596Перезагрузка успешно завершена".HEX()
    val noPermissions: String =
        getHEXString("noPermissions", "#f55442У вас нет прав")
    val onlyForPlayers: String =
        getHEXString("onlyForPlayers", "#f55442Эта команда только для игроков")
    val wrongItemInHand: String =
        getHEXString("wrongItemInHand", "#f55442Предмет в вашей руке не подходит для продажи")
    val wrongArgs: String =
        getHEXString("wrongArgs", "#f55442Неверное использование команды")
    val wrongPrice: String =
        getHEXString("wrongPrice", "#f55442Неверный ценовой диапазон")
    val auctionAdded: String =
        getHEXString("auctionAdded", "#18dbd1Предмет добавлен на аукцион")
    val dbError: String =
        getHEXString("dbError", "#f55442Произошла ошибка")
    val inventoryFull: String =
        getHEXString("inventoryFull", "#f55442Инвентарь полон")
    val ownerCantBeBuyer: String =
        getHEXString("ownerCantBeBuyer", "#f55442Вы не можете купить собственный лот")
    val notEnoughMoney: String =
        getHEXString("notEnoughMoney", "#f55442У вас недостаточно денег")
    val unexpectedError: String =
        getHEXString("unexpectedError", "#f55442Произошла непредвиденная ошибка")
    val failedToPay: String =
        getHEXString("failedToPay", "#f55442Не удалось выплатить деньги")
    val itemBought: String =
        getHEXString("itemBought", "#18dbd1Вы купили предмет")
    val title: String =
        getHEXString("title", "#1382d6Аукцион")
    val back: String =
        getHEXString("back", "#18dbd1Назад")
    val prev: String =
        getHEXString("prev", "#18dbd1Раньше")
    val next: String =
        getHEXString("next", "#18dbd1Дальше")
    val sort: String =
        getHEXString("sort", "#18dbd1Сортировка")
    val announce: String =
        getHEXString("announce", "#18dbd1Игрок %player% выставил на /aauc новый предмет")


    val maxAuctions: String =
        getHEXString("maxAuctions", "#f55442У вас уже макстимальное число лотов")


    val leftButton: String =
        getHEXString("leftButton", "#d6a213ЛКМ #18dbd1- купить")
    val rightButton: String =
        getHEXString("rightButton", "#d6a213ПКМ #18dbd1- убрать")


}