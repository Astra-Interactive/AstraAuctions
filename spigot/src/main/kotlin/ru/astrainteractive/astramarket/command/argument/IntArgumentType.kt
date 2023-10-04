package ru.astrainteractive.astramarket.command.argument

import ru.astrainteractive.astralibs.command.types.ArgumentType

class IntArgumentType(private val default: Int) : ArgumentType<Int> {
    override fun transform(value: String): Int {
        return value.toIntOrNull() ?: default
    }

    object Optional : ArgumentType<Int?> {
        override fun transform(value: String): Int? {
            return value.toIntOrNull()
        }
    }
}
