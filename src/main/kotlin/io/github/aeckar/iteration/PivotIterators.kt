package io.github.aeckar.iteration

import io.github.aeckar.collections.DataPivot
import io.github.aeckar.collections.getOrInsert
import io.github.aeckar.collections.toArrayList
import io.github.aeckar.utils.KoverExclude
import java.io.Reader

/*
    PivotIterator                   interface
        CharPivotIterator           interface
        AbstractPivotIterator       abstract class
 */

// ------------------------------ factories ------------------------------

/**
 * Returns an iterator pivoting over the elements in the list.
 */
public fun <E, V> List<E>.pivotIterator(init: (Int) -> V): PivotIterator<E, Int, V> {
    val revertible = ListRevertibleIterator(this)
    return object : AbstractPivotIterator<E, Int, V>(
        revertible,
        init
    ), RevertibleIterator<E, Int> by revertible {
        override fun peek() = revertible.peek()
    }
}

/**
 * Returns an iterator pivoting over the characters in this string.
 */
public fun <V> String.pivotIterator(init: (Int) -> V): CharPivotIterator<Int, V> {
    val revertible = StringRevertibleIterator(this)
    return object : AbstractPivotIterator<Char, Int, V>(
        revertible,
        init
    ), CharPivotIterator<Int, V>, CharRevertibleIterator<Int> by revertible {
        override fun peek() = revertible.peek()
    }
}

/**
 * Returns an iterator pivoting over the characters in this source, loaded one line at a time.
 *
 * Making this source buffered provides no performance benefit to the returned iterator.
 * If this is [closed][Reader.close],
 * any function called from the returned instance throws an [IllegalStateException].
 */
public fun <E> Reader.pivotIterator(init: () -> E): CharPivotIterator<SourcePosition, E> {
    val revertible = SourceRevertibleIterator(this)
    return object : AbstractPivotIterator<Char, SourcePosition, E>(
        revertible,
        { init() }
    ), CharPivotIterator<SourcePosition, E>, CharRevertibleIterator<SourcePosition> by revertible {
        override fun peek() = revertible.peek()
    }
}

// ------------------------------------------------------------

/**
 * A revertible iterator over a sequence of elements, each of which is assigned some value.
 *
 * Use when it is necessary to map both positional data and metadata to elements in a sequence
 * by using revertible iteration.
 *
 * ```kotlin
 *     val chars = "Hello, world!".pivotIterator { arrayOf(0) }
 *     while (chars.hasNext()) {
 *         chars.here()[0] = chars.nextChar().code
 *     }
 *     println(chars.pivots().map { it.value })    // [72, 101, 108, ... 33]
 * ```
 */
@KoverExclude   // Covered by tests on inheritors
public interface PivotIterator<out E, P : Comparable<P>, V> : RevertibleIterator<E, P> {
    /**
     * Returns the value assigned to the current element.
     */
    public fun here(): V

    /**
     * Returns a list of the previously visited pivots, including the current one.
     *
     * Invoking this function may incur a significant performance impact for large sequences.
     */
    public fun pivots(): List<DataPivot<P, V>>
}

/**
 * An iterator pivoting over a sequence of characters.
 */
@KoverExclude   // Covered by tests on inheritors
public interface CharPivotIterator<P : Comparable<P>, V> : PivotIterator<Char, P, V>, CharRevertibleIterator<P>

/**
 * A basic implementation of a pivoting iterator.
 * @param revertible a revertible iterator over the same sequence of elements
 * @param init returns the default value of each pivot
 */
public abstract class AbstractPivotIterator<out E, P : Comparable<P>, V>(
    private val revertible: RevertibleIterator<E, P>,
    private val init: (P) -> V
) : PivotIterator<E, P, V>, RevertibleIterator<E, P> {
    private var cursor: DataPivot<P, V>? = null

    final override fun here(): V {
        val position = revertible.position()
        val node = cursor?.getOrInsert(revertible.position()) { init(position) }
            ?: DataPivot(revertible.position(), init(position))
        this.cursor = node
        return node.value
    }

    override fun pivots(): List<DataPivot<P, V>> = cursor.toArrayList()
}