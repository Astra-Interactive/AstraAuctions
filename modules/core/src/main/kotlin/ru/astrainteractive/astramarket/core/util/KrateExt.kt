package ru.astrainteractive.astramarket.core.util

import ru.astrainteractive.klibs.kstorage.api.cache.CacheOwner
import kotlin.reflect.KProperty

operator fun <T> CacheOwner<T>.getValue(thisRef: Any, property: KProperty<*>): T {
    return this.cachedValue
}
