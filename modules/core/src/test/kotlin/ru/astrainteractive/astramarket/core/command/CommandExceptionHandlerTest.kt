@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.core.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.argumenttype.FloatArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.exception.LocalizableComponentCommandException
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandExceptionHandlerTest {
    private val sender = RecordingConsoleKCommandSender()
    private val multiplatformCommand = MultiplatformCommand(FakeMultiplatformCommands(sender))
    private val translationKrate = DefaultMutableKrate(
        factory = ::PluginTranslation,
        loader = { null }
    ).asCachedMutableKrate()
    private val handler = CommandExceptionHandler(
        multiplatformCommand = multiplatformCommand,
        translationKrate = translationKrate
    )
    private val error = PluginTranslation().error

    private fun assertSenderReadOnly(message: LocalizableComponent) {
        assertEquals(listOf(message), sender.messages)
    }

    private fun execute(command: LiteralArgumentBuilder<Any>, input: String) {
        val dispatcher = CommandDispatcher<Any>()
        dispatcher.register(command)
        dispatcher.execute(input, Any())
    }

    private fun executeFailing(failure: Throwable) {
        val command = with(multiplatformCommand) {
            command("fail") {
                runs(handler::handle) { _ -> throw failure }
            }
        }
        execute(command, "fail")
    }

    @Test
    fun GIVEN_sender_without_permission_WHEN_command_requires_it_THEN_sender_reads_no_permission() {
        val command = with(multiplatformCommand) {
            command("reload") {
                runs(handler::handle) { ctx -> ctx.requirePermission(PluginPermission.Reload) }
            }
        }

        execute(command, "reload")

        assertSenderReadOnly(error.noPermission)
    }

    @Test
    fun GIVEN_console_WHEN_command_requires_player_THEN_console_reads_only_player_command() {
        val command = with(multiplatformCommand) {
            command("market") {
                runs(handler::handle) { ctx -> ctx.requirePlayer() }
            }
        }

        execute(command, "market")

        assertSenderReadOnly(error.onlyPlayerCommand)
    }

    @Test
    fun GIVEN_argument_that_is_not_a_number_WHEN_command_converts_it_THEN_sender_reads_wrong_usage() {
        val command = with(multiplatformCommand) {
            command("sell") {
                argument("price", StringArgumentType.string()) { priceArg ->
                    runs(handler::handle) { ctx -> ctx.requireArgument(priceArg, FloatArgumentConverter) }
                }
            }
        }

        execute(command, "sell ten")

        assertSenderReadOnly(error.wrongUsage)
    }

    @Test
    fun GIVEN_failure_with_its_own_text_WHEN_command_fails_THEN_sender_reads_that_text() {
        val text = LocalizedText.shared("The lot is already sold")

        executeFailing(LocalizableComponentCommandException(text))

        assertSenderReadOnly(text)
    }

    @Test
    fun GIVEN_unexpected_exception_WHEN_command_fails_THEN_sender_reads_unexpected_error() {
        executeFailing(IllegalStateException("Database is closed"))

        assertSenderReadOnly(error.unexpected)
    }

    @Test
    fun GIVEN_translations_reloaded_WHEN_command_fails_THEN_sender_reads_reloaded_text() {
        val reloadedText = LocalizedText.shared("Reloaded unexpected error")
        translationKrate.save(
            PluginTranslation(error = PluginTranslation.Error(unexpected = reloadedText))
        )

        executeFailing(IllegalStateException("Database is closed"))

        assertSenderReadOnly(reloadedText)
    }
}
