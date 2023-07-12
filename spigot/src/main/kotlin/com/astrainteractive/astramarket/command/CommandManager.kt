import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.command.AuctionCommand
import com.astrainteractive.astramarket.command.di.CommandsModule
import com.astrainteractive.astramarket.command.reloadCommand

class CommandManager(
    plugin: AstraMarket,
    module: CommandsModule
) {

    init {
        reloadCommand(plugin, module)
        AuctionCommand(plugin, module)
    }
}
