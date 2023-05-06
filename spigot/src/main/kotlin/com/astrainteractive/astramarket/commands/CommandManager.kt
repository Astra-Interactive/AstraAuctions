import com.astrainteractive.astramarket.commands.AuctionCommand
import com.astrainteractive.astramarket.commands.reloadCommand

/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see reloadCommand
 */
class CommandManager {
    /**
     * Here you should declare commands for your plugin
     *
     * Commands stored in plugin.yml
     *
     * etemp has TabCompleter
     */
    init {
        reloadCommand()
        AuctionCommand()
    }
}
