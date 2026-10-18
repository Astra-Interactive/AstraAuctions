package ru.astrainteractive.astramarket.data.dao.market.model

sealed class MarketStorageError(message: String, cause: Throwable?) : Exception(message, cause) {
    class QueryFailed(cause: Throwable) : MarketStorageError("A market database query failed", cause)
}
