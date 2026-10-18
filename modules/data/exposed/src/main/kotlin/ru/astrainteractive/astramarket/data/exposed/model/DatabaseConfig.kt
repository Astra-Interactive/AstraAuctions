package ru.astrainteractive.astramarket.data.exposed.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration

@Serializable
internal class DatabaseConfig(
    val configuration: DatabaseConfiguration
)
