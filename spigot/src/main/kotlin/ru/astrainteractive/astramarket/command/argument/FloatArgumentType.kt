package ru.astrainteractive.astramarket.command.argument

import ru.astrainteractive.astralibs.command.types.ArgumentType

class FloatArgumentType(private val default: Float) : ArgumentType<Float> {
    override fun transform(value: String): Float {
        return value.toFloatOrNull() ?: default
    }

    object Optional : ArgumentType<Float?> {
        override fun transform(value: String): Float? {
            return value.toFloatOrNull()
        }
    }
}
