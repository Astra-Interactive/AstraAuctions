package com.astrainteractive.astratemplate.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.Exception
import java.sql.ResultSet
import kotlin.coroutines.CoroutineContext


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


public inline fun <T> callbackCatching(callback: Callback?, block: () -> T?): T? = try {
    block.invoke()
} catch (e: Exception) {
    e.printStackTrace()
    callback?.onFailure(e)
    null
}

fun ItemStack.setDisplayName(name: String) {
    val meta = itemMeta
    meta.setDisplayName(name)
    itemMeta = meta
}


val Player.uuid: String
    get() = this.uniqueId.toString()

fun Player.playSound(sound:String){
    playSound(location,sound,1f,1f)
}