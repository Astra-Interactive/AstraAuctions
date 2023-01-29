package com.astrainteractive.astramarket.plugin

import ru.astrainteractive.astralibs.file_manager.FileManager


/**
 * All plugin files such as config.yml and other should only be stored here!
 * Except for translation.yml - it should be stored at EmpireTranslation
 * @see Translation
 */
object Files {
    val configFile: FileManager =
        FileManager("config.yml")
}