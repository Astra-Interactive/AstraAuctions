package com.astrainteractive.astratemplate.utils

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.catchingNoStackTrace
import com.astrainteractive.astralibs.valueOfOrNull
import org.bukkit.Bukkit

enum class ServerVersion {
    v1_17_R1, v1_18_R1, UNMAINTAINED;

    override fun toString(): String = name

    companion object {
        var version: ServerVersion = UNMAINTAINED
            private set

        fun getServerVersion(): ServerVersion {
            val v = Bukkit.getServer().javaClass.packageName.split(".").last()
            Logger.log("Bukkit version is ${v}", "AstraAuctions")
            version = valueOfOrNull<ServerVersion>(v) ?: UNMAINTAINED
            return version
        }
    }
}

enum class ServerType {
    PAPER, UNMAINTAINED;

    override fun toString(): String = name

    companion object {
        var type: ServerType = UNMAINTAINED
            private set

        fun getServerType(): ServerType {
            if (catchingNoStackTrace { Class.forName("com.destroystokyo.paper.VersionHistoryManager\$VersionData") } != null)
                type = PAPER
            return type
        }
    }
}