import com.astrainteractive.astralibs.*
import com.astrainteractive.astratemplate.commands.AuctionCommand
import com.astrainteractive.astratemplate.commands.ReloadCommand
import com.astrainteractive.astratemplate.utils.*


/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see ReloadCommand
 */
class CommandManager : AsyncTask {
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
        AstraLibs.registerTabCompleter("aauc") { sender, args ->
            if (args.isEmpty())
                return@registerTabCompleter listOf("aauc", "aaucreload")
            if (args.size == 1)
                return@registerTabCompleter listOf("aauc", "aaucreload").withEntry(args.last())
            if (args.size == 2)
                when (args.first()) {
                    "aauc" -> return@registerTabCompleter listOf("sell").withEntry(args.last())
                }
            return@registerTabCompleter listOf<String>()
        }


    }

}