package io.github.aeckar.collections

import io.github.aeckar.iteration.fromLast

// ------------------------------ factories ------------------------------

/**
 * Returns a doubly-linked list node containing the given element.
 */
public fun <E> listNodeOf(element: E): DataListNode<E> = DataListNode(element)

/**
 * Returns the head of the doubly-linked list created with the given elements.
 *
 * For a single element, prefer [listNodeOf].
 * Throws an exception if no elements are supplied.
 */
public fun <E> headOf(vararg elements: E): DataListNode<E> {
    return elements.fromLast().asSequence()
        .map { DataListNode(it) }
        .reduce { head, node ->
            head.insertBefore(node)
            node
        }
}

/**
 * Returns the tail of the doubly-linked list created with the given elements.
 *
 * For a single element, prefer [listNodeOf].
 * Throws an exception if no elements are supplied.
 */
public fun <E> tailOf(vararg elements: E): DataListNode<E> {
    return elements.asSequence()
        .map { DataListNode(it) }
        .reduce { tail, node ->
            tail.insertAfter(node)
            node
        }
}

// ------------------------------ implementations ------------------------------

/**
 * Returns a list containing all nodes in this linked list.
 *
 * The returned list is not guaranteed to be read-only.
 * If the receiver is null, an empty list is returned.
 */
public fun <Self: ListNode<Self>> Self?.toArrayList(): List<Self> {
    this ?: return listOf()
    val elements = this
    return mutableListOf<Self>().apply {
        addAll(elements.downToHead())
        reverse()
        next?.let { addAll(it) }
    }
}

/**
 * Returns an iterator over every element in this linked list, up to and including this node,
 * starting from this node.
 */
@Suppress("UNCHECKED_CAST")
public fun <Self : ListNode<Self>> ListNode<Self>?.downToHead(): Iterable<Self> = Iterable {
    object : Iterator<Self> {
        var cursor: Self? = this@downToHead as Self

        override fun hasNext() = cursor != null

        override fun next(): Self {
            val cursor = cursor ?: throw NoSuchElementException("Node is head")
            this.cursor = cursor.last
            return cursor
        }
    }
}

/**
 * A node in some larger, doubly-linked list.
 *
 * Unlike in Java, this library does not provide a dedicated linked list class.
 * Instead, list nodes are operated on directly.
 *
 * A nullable property of this type whose value is null is considered an empty linked list.
 * Iterates over the elements in the linked list, starting from and including this one.
 *
 * ```kotlin
 *     val nodes = link(values(), 1, 2, 3)
 *     println(nodes.toList())   // [1, 2, 3]
 * ```
 */
@Suppress("UNCHECKED_CAST")
public abstract class ListNode<Self : ListNode<Self>> : Node<Self> {
    @PublishedApi internal var next: Self? = null
    internal var last: Self? = null

    /**
     * Returns the next node in the linked list.
     * @throws NoSuchElementException this is the tail of the list
     */
    public fun next(): Self {
        return next ?: throw NoSuchElementException("Node is tail")
    }

    /**
     * Returns the next node in the linked list, or null if this is the tail of the list.
     */
    public fun nextOrNull(): Self? = next

    /**
     * Returns the previous node in the linked list.
     * @throws NoSuchElementException this is the head of the list
     */
    public fun last(): Self {
        return last ?: throw NoSuchElementException("Node is head")
    }

    /**
     * Returns the previous node in the linked list, or null if this is the head of the list.
     */
    public fun lastOrNull(): Self? = last

    /**
     * Inserts the given node directly after this one.
     * @throws IllegalArgumentException [node] is this same instance
     */
    public fun insertAfter(node: Self) {
        require(this !== node) { "Cannot append node to itself" }
        next?.apply { last = node }
        node.next = next
        node.last = this as Self
        next = node
    }

    /**
     * Inserts the given node directly before this one.
     * @throws IllegalArgumentException [node] is this same instance
     */
    public fun insertBefore(node: Self) {
        require(this !== node) { "Cannot append node to itself" }
        last?.apply { next = node }
        node.last = last
        node.next = this as Self
        last = node
    }

    /**
     * Returns the head of this linked list.
     */
    public fun head(): Self = downToHead().last()

    /**
     * Returns the tail of this linked list.
     */
    public fun tail(): Self = asIterable().last()

    /**
     * Returns the node in this linked list that satisfies the given predicate,
     * or the tail of the list if one is not found.
     */
    public inline fun seek(predicate: (Self) -> Boolean): Self {
        var tail = this as Self
        for (element in this) {
            if (predicate(element)) {
                return element
            }
            tail = element
        }
        return tail
    }

    /**
     * Returns the node in this linked list that satisfies the given predicate,
     * in reverse order, or the head of the list if one is not found.
     */
    public inline fun backtrace(predicate: (Self) -> Boolean): Self {
        var head = this as Self
        for (element in downToHead()) {
            if (predicate(element)) {
                return element
            }
            head = element
        }
        return head
    }

    /**
     * Returns a string containing the entire linked list whose head is this node.
     */
    override fun linkedString(): String = joinToString(prefix = "[", postfix = "]", transform = Node<*>::nodeString)

    /**
     * Returns an iterator over every element in this linked list, up to and including this node.
     */
    override fun iterator(): Iterator<Self> = object : Iterator<Self> {
        var cursor: Self? = this@ListNode as Self

        override fun hasNext() = cursor != null

        override fun next(): Self {
            val cursor = cursor ?: throw NoSuchElementException("Node is tail")
            this.cursor = cursor.next
            return cursor
        }
    }
}

/**
 * A doubly-linked list node with an assigned value.
 */
public class DataListNode<E> internal constructor(  // Use factory
    override val element: E
) : ListNode<DataListNode<E>>(), DataNode<DataListNode<E>, E> {
    /**
     * Inserts a node containing the given element directly after this one.
     * @return the node containing the element
     */
    public fun insertAfter(element: E): DataListNode<E> {
        return DataListNode(element).also { super.insertAfter(it) }
    }

    /**
     * Inserts a node containing the given element directly before this one.
     * @return the node containing the element
     */
    public fun insertBefore(element: E): DataListNode<E> {
        return DataListNode(element).also { super.insertBefore(it) }
    }

    /**
     * Returns true if the elements contained by both objects are equal.
     */
    override fun equals(other: Any?): Boolean {
        if (other is Collection<*>) {
            return other.size == 1 && other.single() == element
        }
        return other is DataListNode<*> && element == other.element
    }

    override fun hashCode(): Int = element.hashCode()
    override fun toString(): String = "{ $element }"
}