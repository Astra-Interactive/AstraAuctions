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
        null//throw Exception("Database not initialized")
    else if (!Database.isUpdated)
        null//throw Exception("Database not updated")
    else block.invoke()
} catch (e: Exception) {
    com.astrainteractive.astralibs.Logger.error(e.stackTraceToString(), "Database")
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

fun ItemStack.displayNameOrMaterialName(): String {
    val name = itemMeta!!.displayName
    if (name.isNullOrEmpty())
        return type.name
    return name
}

inline fun <reified T : kotlin.Enum<T>> T.addIndex(offset: Int): T {
    val values = T::class.java.enumConstants
    var res = ordinal + offset
    if (res <= -1) res = values.size-1
    val index = res % values.size
    return values[index]
}

inline fun <reified T : kotlin.Enum<T>> T.next(): T {
    return addIndex(1)
}

inline fun <reified T : kotlin.Enum<T>> T.prev(): T {
    return addIndex(-1)
}
