package ru.astrainteractive.astramarket.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration

@Serializable
internal class DatabaseConfig(
    val configuration: DatabaseConfiguration = DatabaseConfiguration.H2("MARKET")
)
