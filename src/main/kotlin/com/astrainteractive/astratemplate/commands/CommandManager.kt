import com.astrainteractive.astralibs.*
import com.astrainteractive.astratemplate.commands.AuctionCommand
import com.astrainteractive.astratemplate.commands.ReloadCommand
import com.astrainteractive.astratemplate.utils.*


/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see ReloadCommand
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
        ReloadCommand()
        AuctionCommand()



    }

}