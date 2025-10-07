package ru.astrainteractive.astramarket.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration

@Serializable
internal class DatabaseConfig(
    val configuration: DatabaseConfiguration
)
