package ru.astrainteractive.astramarket.core.itemstack

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.encoding.model.EncodedObject

internal class ItemStackEncoderImpl(private val encoder: ObjectEncoder) : ItemStackEncoder {
    override fun toItemStack(
        encodedObject: EncodedObject
    ): Result<ItemStack> = runCatching {
        when (encodedObject) {
            is EncodedObject.Base64 -> encoder.fromBase64(encodedObject)
            is EncodedObject.ByteArray -> runCatching {
                ItemStack.deserializeBytes(encodedObject.value)
            }.getOrNull() ?: encoder.fromByteArray(encodedObject)
        }
    }

    override fun toByteArray(
        itemStack: ItemStack
    ): Result<EncodedObject.ByteArray> = runCatching {
        encoder.toByteArray(itemStack)
    }
}
