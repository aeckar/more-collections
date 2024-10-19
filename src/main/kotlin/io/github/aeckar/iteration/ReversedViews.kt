package io.github.aeckar.iteration

import io.github.aeckar.utils.KoverExclude

/**
 * Returns an iterable object over the elements in this list in reverse order.
 *
 * Enables reverse iteration using sequences.
 */
@KoverExclude
public fun <E> List<E>.fromLast(): Iterable<E> = Iterable {
    object : PositionalIterator<E>(lastIndex) {
        override fun hasNext() = position >= 0
        override fun next() = this@fromLast[position--]
    }
}

/**
 * Returns an iterable object over the elements in this array in reverse order.
 *
 * Enables reverse iteration using sequences.
 */
public fun <T> Array<T>.fromLast(): Iterable<T> = Iterable {
    object : PositionalIterator<T>(lastIndex) {
        override fun hasNext() = position >= 0
        override fun next() = this@fromLast[position--]
    }
}