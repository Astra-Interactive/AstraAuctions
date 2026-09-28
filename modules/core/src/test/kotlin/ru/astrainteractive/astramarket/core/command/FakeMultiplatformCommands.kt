package ru.astrainteractive.astramarket.core.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommands
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KCommandSender

/**
 * Builds plain Brigadier nodes and attributes every command to [sender]. A `null` [sender] stands for one the
 * platform cannot wrap, such as a command block: resolving it throws, like the real implementations do.
 */
internal class FakeMultiplatformCommands(
    private val sender: KCommandSender?
) : MultiplatformCommands {
    override fun literal(literal: String): LiteralArgumentBuilder<Any> {
        return LiteralArgumentBuilder.literal(literal)
    }

    override fun <T : Any> argument(
        name: String,
        argumentType: ArgumentType<T>
    ): RequiredArgumentBuilder<Any, T> {
        return RequiredArgumentBuilder.argument(name, argumentType)
    }

    override fun getSender(context: CommandContext<*>): KCommandSender {
        return sender ?: error("Could not wrap sender")
    }
}
