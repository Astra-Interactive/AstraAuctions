package ru.astrainteractive.astramarket.market.fake

import ru.astrainteractive.astralibs.economy.EconomyFacade
import java.util.UUID

class FakeEconomyFacade(
    balances: Map<UUID, Double>
) : EconomyFacade {
    private val balances = balances.toMutableMap()
    val failingDeposits = mutableSetOf<UUID>()

    fun balanceOf(uuid: UUID): Double = balances.getOrDefault(uuid, 0.0)

    override suspend fun getBalance(uuid: UUID): Double? = balances[uuid]

    override suspend fun takeMoney(uuid: UUID, amount: Double): Boolean {
        if (balanceOf(uuid) < amount) return false
        balances[uuid] = balanceOf(uuid) - amount
        return true
    }

    override suspend fun addMoney(uuid: UUID, amount: Double): Boolean {
        if (uuid in failingDeposits) return false
        balances[uuid] = balanceOf(uuid) + amount
        return true
    }

    override suspend fun hasAtLeast(uuid: UUID, amount: Double): Boolean = balanceOf(uuid) >= amount
}
