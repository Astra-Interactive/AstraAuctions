import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.commands.AuctionCommand
import com.astrainteractive.astramarket.commands.di.CommandsModule
import com.astrainteractive.astramarket.commands.reloadCommand

class CommandManager(
    plugin: AstraMarket,
    module: CommandsModule
) {

    init {
        reloadCommand(plugin, module)
        AuctionCommand(plugin, module)
    }
}
