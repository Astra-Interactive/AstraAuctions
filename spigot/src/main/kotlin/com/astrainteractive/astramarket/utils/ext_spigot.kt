package com.astrainteractive.astramarket.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.BukkitAsync
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.menu.menu.Menu


fun ItemStack.setDisplayName(name: String) {
    val meta = itemMeta!!
    meta.setDisplayName(name)
    itemMeta = meta
}

fun Player.playSound(sound: String) {
    playSound(location, sound, 1f, 1f)
}

fun ItemStack.displayNameOrMaterialName(): String {
    val name = itemMeta!!.displayName
    if (name.isNullOrEmpty())
        return type.name
    return name
}

suspend fun Menu.openSync() = withContext(Dispatchers.BukkitMain){
    open()
}