package ru.astrainteractive.astramarket.util

internal fun <T> List<T>.nextAfter(current: T, backwards: Boolean): T {
    val index = indexOf(current)
    if (index == -1) return first()
    val offset = if (backwards) -1 else 1
    return get(index.plus(offset).mod(size))
}
