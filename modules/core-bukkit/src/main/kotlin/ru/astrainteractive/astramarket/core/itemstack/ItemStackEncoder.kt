package ru.astrainteractive.astramarket.core.itemstack

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.model.EncodedObject

interface ItemStackEncoder {
    fun toItemStack(encodedObject: EncodedObject): Result<ItemStack>
    fun toByteArray(itemStack: ItemStack): Result<EncodedObject.ByteArray>
}
