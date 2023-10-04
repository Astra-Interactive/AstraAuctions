package ru.astrainteractive.astramarket.command.argument

import ru.astrainteractive.astralibs.command.types.ArgumentType

class LabelArgument(private val label: String) : ArgumentType<Unit> {
    override fun transform(value: String) {
        if (label != value) error("Wrong label")
    }
}
