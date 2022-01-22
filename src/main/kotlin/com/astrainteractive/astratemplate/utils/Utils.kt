package com.astrainteractive.astratemplate.utils

import com.astrainteractive.astratemplate.sqldatabase.Database
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet


/**
 * For loop for ResultSet
 */
inline fun ResultSet.forEach(rs: (ResultSet) -> Unit) {
    while (this.next()) {
        rs(this)
    }
}


public inline fun <R : Any> ResultSet.mapNotNull(rs: (ResultSet) -> R?): List<R> {
    return mapNotNullTo(ArrayList<R>(), rs)
}

public inline fun <R : Any, C : MutableCollection<in R>> ResultSet.mapNotNullTo(
    destination: C,
    rs: (ResultSet) -> R?
): C {
    forEach { element -> rs(element)?.let { destination.add(it) } }
    return destination
}


public inline fun <T> callbackCatching(block: () -> T?): T? = try {
    if (!Database.isInitialized)
        throw Exception("Database not initialized")
    if (!Database.isUpdated)
        throw Exception("Database not updated")
    block.invoke()
} catch (e: Exception) {
    com.astrainteractive.astralibs.Logger.error(e.stackTraceToString(),"Database")
    null
}

fun ItemStack.setDisplayName(name: String) {
    val meta = itemMeta!!
    meta.setDisplayName(name)
    itemMeta = meta
}


val Player.uuid: String
    get() = this.uniqueId.toString()

fun Player.playSound(sound: String) {
    playSound(location, sound, 1f, 1f)
}

fun ItemStack.displayNameOrMaterialName(): String{
        val name = itemMeta!!.displayName
        if (name.isNullOrEmpty())
            return type.name
        return name
    }