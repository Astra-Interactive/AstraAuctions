package com.astrainteractive.astratemplate.utils

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object NMSHelper {

    fun serializeItem(itemStack: ItemStack): ByteArray {
        val io = ByteArrayOutputStream()
        val os = BukkitObjectOutputStream(io)
        os.writeObject(itemStack)
        os.flush()
        return io.toByteArray()
    }

    fun deserializeItem(byteArray: ByteArray,createdTime:Long): ItemStack {
        if (ServerType.type == ServerType.PAPER && createdTime < 1641877447314L) {
            return ItemStack.deserializeBytes(byteArray)
        }
        val _in = ByteArrayInputStream(byteArray)
        val _is = BukkitObjectInputStream(_in)
        return _is.readObject() as ItemStack
    }

}