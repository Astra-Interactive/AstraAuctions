import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astratemplate.AstraAuctions
import com.astrainteractive.astratemplate.gui.AuctionGui
import com.astrainteractive.astratemplate.sqldatabase.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.AsyncTask
import com.astrainteractive.astratemplate.utils.Callback
import com.astrainteractive.astratemplate.utils.Permissions
import com.astrainteractive.astratemplate.utils.Translation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.Exception
import kotlin.math.min


/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see Reload
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
        AstraLibs.registerTabCompleter("atemp") { sender, args ->
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

    val reload = AstraLibs.registerCommand("aaucreload") { sender, args ->
        if (!sender.hasPermission(Permissions.reload))
            return@registerCommand
        sender.sendMessage(AstraAuctions.translations.reloadStarted)
        AstraAuctions.instance.reloadPlugin()
        sender.sendMessage(AstraAuctions.translations.reloadSuccess)
    }

    private fun CommandSender.checkPlayer(): Boolean = if (this !is Player) {
        sendMessage(Translation.instanse.onlyForPlayers)
        false
    } else {
        true
    }

    private fun CommandSender.checkPermission(permission: String): Boolean = if (!this.hasPermission(permission)) {
        sendMessage(Translation.instanse.noPermissions)
        false
    } else {
        true
    }

    val aauc = AstraLibs.registerCommand("aauc") { sender, args ->
        if (!sender.checkPermission(Permissions.sell))
            return@registerCommand
        if (!sender.checkPlayer())
            return@registerCommand
        val player = sender as Player
        val cmd = args.getOrNull(0)
        if (cmd == null) {
            launch(Dispatchers.IO) {
                AuctionGui(AstraPlayerMenuUtility(sender)).open()

            }

            return@registerCommand
        }
        when (cmd) {
            "sell" -> {
                sell(sender, args)
            }
        }
    }

    private fun sell(player: Player, args: Array<out String>) {
        launch {
            val auctionsAmount = Repository.countPlayerAuctions(player)
            if (auctionsAmount ?: 0 > AstraAuctions.pluginConfig.auction.maxAuctionPerPlayer) {
                player.sendMessage(Translation.instanse.maxAuctions)
                return@launch
            }
            val price = args.getOrNull(1)?.toFloatOrNull()
            var amount = args.getOrNull(2)?.toIntOrNull() ?: 1
            val item = player.inventory.itemInMainHand
            amount = min(item.amount, amount)

            if (price == null) {
                player.sendMessage(Translation.instanse.wrongArgs)
                return@launch
            }
            if (price > AstraAuctions.pluginConfig.auction.maxPrice || price < AstraAuctions.pluginConfig.auction.minPrice) {
                player.sendMessage(Translation.instanse.wrongPrice)
                return@launch
            }

            if (item == null || item.type == Material.AIR) {
                player.sendMessage(Translation.instanse.wrongItemInHand)
                return@launch
            }
            val itemClone = item.clone().apply { this.amount = amount }
            val auction = Auction(player, itemClone, price)

            Repository.insertAuction(auction, object : Callback() {
                override fun <T> onSuccess(result: T?) {
                    item.amount -= amount
                    player.sendMessage(Translation.instanse.auctionAdded)
                    if (AstraAuctions.pluginConfig.auction.announce)
                        Bukkit.broadcastMessage(Translation.instanse.announce.replace("%player%",player.name))
                }

                override fun onFailure(e: Exception) {
                    player.sendMessage(Translation.instanse.dbError + "${e.message}")
                }

            })
        }


    }
}