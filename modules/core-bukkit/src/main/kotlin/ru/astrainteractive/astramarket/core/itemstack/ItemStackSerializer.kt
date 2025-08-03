package ru.astrainteractive.astramarket.core.itemstack

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

object ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "AstraLibs.ItemStack.Json",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: ItemStack) {
        val string = encodeToString(value)
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        val string = decoder.decodeString()
        return decodeFromString(string).getOrThrow()
    }

    private const val KEY = "item_stack"

    fun encodeToString(itemStack: ItemStack): String {
        val configuration = YamlConfiguration()
        configuration.set(KEY, itemStack)
        return configuration.saveToString()
    }

    fun decodeFromString(string: String): Result<ItemStack> {
        return runCatching {
            println(string)
            val configuration = YamlConfiguration()
            configuration.loadFromString(string)
            configuration.getItemStack(KEY)
                ?: error("Could not get item stack from string: $string")
        }
    }
}