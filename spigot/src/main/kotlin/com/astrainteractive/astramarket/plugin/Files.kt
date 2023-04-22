package com.astrainteractive.astramarket.plugin

import ru.astrainteractive.astralibs.filemanager.SpigotFileManager


/**
 * All plugin files such as config.yml and other should only be stored here!
 * Except for translation.yml - it should be stored at EmpireTranslation
 * @see Translation
 */
object Files {
    val configFile = SpigotFileManager("config.yml")
}