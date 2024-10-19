package io.github.aeckar.collections

import io.github.aeckar.utils.KoverExclude

// Read-only view over an ordinary tree node impossible due to limitations on generics
// Sealed hierarchy ensures read-only contract

// ------------------------------ factories ------------------------------

/**
 * Returns a read-only view over these elements.
 */
@KoverExclude
public fun <E> List<E>.readOnly(): ReadOnlyList<E> = ListView(this)

/**
 * Returns a read-only view over these elements.
 */
@KoverExclude
public fun <E> Set<E>.readOnly(): ReadOnlySet<E> = SetView(this)

/**
 * Returns a read-only view over these elements.
 */
@KoverExclude
public fun <E> MultiSet<E>.readOnly(): ReadOnlyMultiSet<E> = MultiSetView(this)

/**
 * Returns a read-only view over this map.
 */
@KoverExclude
public fun <K, V> Map<K, V>.readOnly(): ReadOnlyMap<K, V> = MapView(this)

/**
 * Returns a read-only view over this tree.
 */
@KoverExclude
public fun <E> DataTreeNode<E>.readOnly(): ReadOnlyDataTreeNode<E> = DataTreeNodeView(this)

/**
 * Returns a read-only view over these elements.
 */
@KoverExclude
public fun BooleanList.readOnly(): ReadOnlyBooleanList = BooleanListView(this)

/**
 * Returns a read-only view over these elements.
 */
@KoverExclude
public fun IntList.readOnly(): ReadOnlyIntList = IntListView(this)

/**
 * Returns a read-only view over these elements.
 */
@KoverExclude
public fun LongList.readOnly(): ReadOnlyLongList = LongListView(this)

/**
 * Returns a read-only view over these elements.
 */
@KoverExclude
public fun DoubleList.readOnly(): ReadOnlyDoubleList = DoubleListView(this)

// ------------------------------ read-only interfaces ------------------------------

/**
 * A read-only view of another sequence of elements.
 */
public sealed interface ReadOnlyCollection<E> : Collection<E>

/**
 * A read-only view of another list.
 */
public sealed interface ReadOnlyList<E> : ReadOnlyCollection<E>, List<E> {
    override fun subList(fromIndex: Int, toIndex: Int): ReadOnlyList<E>
}

/**
 * A read-only view over another set.
 */
public sealed interface ReadOnlySet<E> : Set<E>

/**
 * A read-only view over another multiset.
 */
public sealed interface ReadOnlyMultiSet<E> : MultiSet<E>, ReadOnlySet<E>

/**
 * A read-only view over another map.
 */
public sealed interface ReadOnlyMap<K, V> : Map<K, V>

/**
 * A read-only view over another, data-containing, tree node.
 *
 * Disallows modification of the [element] contained.
 */
@KoverExclude
public sealed interface ReadOnlyDataTreeNode<V> : TreeNode<ReadOnlyDataTreeNode<V>>, DataNode<ReadOnlyDataTreeNode<V>, V>

/**
 * A read-only list of unboxed boolean values.
 */
public sealed interface ReadOnlyBooleanList : BooleanList

/**
 * A read-only list of unboxed integers.
 */
public sealed interface ReadOnlyIntList : IntList

/**
 * A read-only list of unboxed long integers.
 */
public sealed interface ReadOnlyLongList : LongList

/**
 * A read-only list of unboxed floating-point numbers.
 */
public sealed interface ReadOnlyDoubleList : DoubleList

// ------------------------------ view implementations ------------------------------

private abstract class View(private val original: Any) {
    final override fun toString() = original.toString()
    final override fun equals(other: Any?) = original == other
    final override fun hashCode() = original.hashCode()
}

private class SetView<E>(original: Set<E>) : View(original), ReadOnlySet<E>, Set<E> by original

private class MultiSetView<E>(original: MultiSet<E>) : View(original), ReadOnlyMultiSet<E>, MultiSet<E> by original

private class MapView<K, V>(original: Map<K, V>) : View(original), ReadOnlyMap<K, V>, Map<K, V> by original

private class ListView<E>(
    private val original: List<E>   // Prefer over override
) : View(original), ReadOnlyList<E>, List<E> by original {
    override fun subList(fromIndex: Int, toIndex: Int): ReadOnlyList<E> {
        return original.subList(fromIndex, toIndex).readOnly()
    }
}

private class DataTreeNodeView<V>(
    private val original: DataTreeNode<V>
) : View(original), ReadOnlyDataTreeNode<V> {
    override val element: V get() = original.element
    override val children: List<ReadOnlyDataTreeNode<V>> = super.children.readOnly()
}

private class BooleanListView(original: BooleanList) : View(original), ReadOnlyBooleanList, BooleanList by original

private class IntListView(original: IntList) : View(original), ReadOnlyIntList, IntList by original

private class LongListView(original: LongList) : View(original), ReadOnlyLongList, LongList by original

private class DoubleListView(original: DoubleList) : View(original), ReadOnlyDoubleList, DoubleList by original