package ru.astrainteractive.astramarket.core.itemstack

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.model.EncodedObject

interface ItemStackEncoder {
    fun toItemStack(encodedObject: EncodedObject): ItemStack
    fun toByteArray(itemStack: ItemStack): EncodedObject.ByteArray
    fun toBase64(itemStack: ItemStack): EncodedObject.Base64
}
