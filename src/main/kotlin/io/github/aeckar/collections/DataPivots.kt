package io.github.aeckar.collections

import io.github.aeckar.iteration.fromLast
import io.github.aeckar.utils.KoverExclude

// Do not confuse collection elements with pivot values
// Each pivot value is unique to its element
// E is always `out`, V is always invariant

// ------------------------------ factories ------------------------------

/**
 * Returns a [DataPivot] with the given position and value.
 */
@KoverExclude
public fun <P : Comparable<P>, V> pivotOf(position: P, value: V): DataPivot<P, V> {
    return DataPivot(position, value)
}

/**
 * Returns the head of the doubly-linked list of pivots
 * containing the given positions and values.
 *
 * For a single pivot, prefer [pivotOf].
 * Throws an exception if no pairs are supplied.
 */
public fun <P : Comparable<P>, V> headPivotOf(vararg pairs: Pair<P, V>): DataPivot<P, V> {
    return pairs.fromLast().asSequence()
        .map { (position, value) -> DataPivot(position, value) }
        .reduce { head, node ->
            head.insertBefore(node)
            node
        }
}

/**
 * Returns the tail of the doubly-linked list of pivots
 * containing the given positions and values.
 *
 * For a single pivot, prefer [pivotOf].
 * Throws an exception if no pairs are supplied.
 */
public fun <P : Comparable<P>, V> tailPivotOf(vararg pairs: Pair<P, V>): DataPivot<P, V> {
    return pairs.asSequence()
        .map { (position, value) -> DataPivot(position, value) }
        .reduce { tail, node ->
            tail.insertAfter(node)
            node
        }
}

// ------------------------------------------------------------

/**
 * An object containing a position and a value.
 * @param position the location of this in some larger object
 * @param value a value unique to this position
 */
public class DataPivot<P : Comparable<P>, V> internal constructor(  // Use factory
    public val position: P,
    public val value: V
) : ListNode<DataPivot<P, V>>() {
    /**
     * Returns true if both positions and values are equal.
     */
    override fun equals(other: Any?): Boolean {
        return other is DataPivot<*, *> && position == other.position && value == other.value
    }

    override fun hashCode(): Int {
        var result = 31
        result = 31 * result + position.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString(): String = "$value @ $position"
}

/**
 * Returns the pivot whose position has a [total ordering][Comparable] equal to the one given.
 *
 * If one does not exist, it is inserted according to the ordering of [P].
 */
public fun <V, P : Comparable<P>> DataPivot<P, V>.getOrInsert(position: P, lazyValue: () -> V): DataPivot<P, V> {
    /**
     * Assumes positions are not equal.
     */
    fun DataPivot<P, V>.insertClosest(position: P): DataPivot<P, V> {
        val pivot = DataPivot(position, lazyValue())
        if (this.position > position) {
            insertBefore(pivot)
        } else {
            insertAfter(pivot)
        }
        return pivot
    }

    if (position.compareTo(this.position) == 0) {
        return this
    }
    var node = this
    if (this.position > position) {
        node = node.backtrace { node.position <= position }
        if (position.compareTo(node.position) == 0) {
            return node
        }
        return node.insertClosest(position)
    }
    node = node.seek { node.position >= position }
    if (position.compareTo(node.position) == 0) {
        return node
    }
    return node.insertClosest(position)
}