package ru.astrainteractive.astramarket.gui.bukkit.util

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.encoding.model.EncodedObject

fun ObjectEncoder.toItemStack(encodedObject: EncodedObject): Result<ItemStack> {
    return runCatching {
        when (encodedObject) {
            is EncodedObject.Base64 -> fromBase64(encodedObject)
            is EncodedObject.ByteArray -> runCatching { ItemStack.deserializeBytes(encodedObject.value) }
                .getOrNull()
                ?: fromByteArray(encodedObject)
        }
    }
}
