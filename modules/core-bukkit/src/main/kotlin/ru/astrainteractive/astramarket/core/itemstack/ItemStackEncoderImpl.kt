package ru.astrainteractive.astramarket.core.itemstack

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.encoding.model.EncodedObject

internal class ItemStackEncoderImpl(private val encoder: ObjectEncoder) : ItemStackEncoder {
    override fun toItemStack(encodedObject: EncodedObject): ItemStack {
        return when (encodedObject) {
            is EncodedObject.Base64 -> encoder.fromBase64(encodedObject)
            is EncodedObject.ByteArray -> encoder.fromByteArray(encodedObject)
        }
    }

    override fun toByteArray(itemStack: ItemStack): EncodedObject.ByteArray {
        return encoder.toByteArray(itemStack)
    }

    override fun toBase64(itemStack: ItemStack): EncodedObject.Base64 {
        return encoder.toBase64(itemStack)
    }
}
