package ru.astrainteractive.astramarket.gui.util

import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal fun OnlineKPlayer.playSound(sound: String) {
    this.tryCast<BukkitOnlineKPlayer>()?.instance?.playSound(sound)
}

internal fun OnlineKPlayer.closeInventory() {
    this.tryCast<BukkitOnlineKPlayer>()?.instance?.closeInventory()
}
