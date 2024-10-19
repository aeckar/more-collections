package io.github.aeckar.iteration

/**
 * Invokes [other] when this is true, returning the receiver.
 */
public inline infix fun Boolean.implies(other: () -> Unit): Boolean {
    if (this) {
        other()
    }
    return this
}

/**
 * Returns the logical disjunction of all values according to the transformation.
 *
 * If this object contains no elements, false is returned.
 * If it contains only a single element, the single result of [transform] is returned.
 */
public inline fun <T> Iterable<T>.orEach(transform: (T) -> Boolean): Boolean {
    var result = false
    forEach { result = result or transform(it) }
    return result
}

/**
 * Returns the logical conjunction of all values according to the transformation.
 *
 * If this object contains no elements, true is returned.
 * If it contains only a single element, the single result of [transform] is returned.
 */
public inline fun <T> Iterable<T>.andEach(transform: (T) -> Boolean): Boolean {
    var result = true
    forEach { result = result and transform(it) }
    return result
}

/**
 * Returns the logical disjunction (`||`) of all values according to the transformation.
 *
 * If this object contains no elements, false is returned.
 * If it contains only a single element, the single result of [transform] is returned.
 */
public inline fun <T> MutableIterable<T>.orEachRemovable(transform: Removable.(T) -> Boolean): Boolean {
    var result = false
    val elements = iterator()
    val removable = elements.asRemovable()
    while (elements.hasNext()) {
        result = result or transform(removable, elements.next())
    }
    return result
}

/**
 * Returns the logical conjunction (`&&`) of all values according to the transformation.
 *
 * If this object contains no elements, true is returned.
 * If it contains only a single element, the single result of [transform] is returned.
 */
public inline fun <T> MutableIterable<T>.andEachRemovable(transform: Removable.(T) -> Boolean): Boolean {
    var result = true
    val elements = iterator()
    val removable = elements.asRemovable()
    while (elements.hasNext()) {
        result = result and transform(removable, elements.next())
    }
    return result
}