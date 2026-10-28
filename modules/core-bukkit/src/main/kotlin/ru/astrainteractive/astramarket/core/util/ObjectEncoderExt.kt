package ru.astrainteractive.astramarket.core.util

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.encoding.model.EncodedObject

fun ObjectEncoder.encodeItemStack(itemStack: ItemStack): Result<EncodedObject.ByteArray> {
    return runCatching {
        toByteArray(itemStack)
    }
}
