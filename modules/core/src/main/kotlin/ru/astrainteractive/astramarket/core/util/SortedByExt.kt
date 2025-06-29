package ru.astrainteractive.astramarket.core.util

inline fun <T, R : Comparable<R>> Iterable<T>.sortedBy(
    isAsc: Boolean,
    crossinline selector: (T) -> R?
): List<T> {
    return if (isAsc) {
        sortedBy(selector)
    } else {
        sortedByDescending(selector)
    }
}
