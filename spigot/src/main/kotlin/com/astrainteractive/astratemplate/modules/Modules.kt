package com.astrainteractive.astratemplate.modules

import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.utils.encoding.BukkitInputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.BukkitOutputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer

object Modules {
    val bukkitSerializer = module {
        Serializer(BukkitOutputStreamProvider, BukkitInputStreamProvider)
    }
}