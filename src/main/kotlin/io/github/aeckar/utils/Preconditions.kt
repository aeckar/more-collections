package io.github.aeckar.utils

/**
 * If the given lambda throws an [IndexOutOfBoundsException],
 * throws a [NoSuchElementException] how the iterator is exhausted.
 */
internal inline fun <R> orNoSuchElement(access: () -> R): R {
    return try {
        access()
    } catch (_: IndexOutOfBoundsException) {
        throw NoSuchElementException("Iterator is exhausted")
    }
}