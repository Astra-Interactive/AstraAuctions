package com.astrainteractive.astratemplate.utils

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


class VaultHook {
    companion object {
        lateinit var instance: VaultHook
        private var econ: Economy? = null
        /**
         * @param  player player
         * @return double - current balance of [player]
         */
        fun getBalance(player: OfflinePlayer): Double? {
            return econ?.getBalance(player)
        }

        /**
         * @param  player player
         * @param  amount amount to take from balance
         * @return boolean - true if [amount] has been taken false if not
         */
        fun takeMoney(player: OfflinePlayer, amount: Double): Boolean {
            val maxBalance = getBalance(player) ?: return false
            if (amount > maxBalance)
                return false
            val response = econ?.withdrawPlayer(player, amount)
            return (response?.type == EconomyResponse.ResponseType.SUCCESS)
        }
        /**
         * @param  player player
         * @param  amount amount to add to balance
         * @return boolean - true if [amount] has been added false if not
         */
        fun addMoney(player: OfflinePlayer,amount: Double):Boolean{
            val response = econ?.depositPlayer(player, amount)
            return (response?.type == EconomyResponse.ResponseType.SUCCESS)
        }
    }

    private fun onEnable() {
        instance = this
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            println("Vault is not installed!")
            return
        }
        val rsp = getServer().servicesManager.getRegistration(Economy::class.java) ?: return
        econ = rsp.provider
    }

    init {
        onEnable()
    }

}