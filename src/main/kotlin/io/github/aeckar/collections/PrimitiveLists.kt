@file:Suppress("MemberVisibilityCanBePrivate")
package io.github.aeckar.collections

import io.github.aeckar.utils.KoverExclude

// Byte, Char, Short, Float lists not implemented as not used as often

private const val DEFAULT_SIZE = 10 // Default size of Java list
private const val GROWTH_FACTOR = 2

// ------------------------------ list interfaces ------------------------------

/**
 * A list of unboxed primitive values.
 */
public sealed interface PrimitiveList {
    /**
     * The number of elements in this list.
     */
    public val size: Int
}

/**
 * A list of unboxed boolean values.
 */
public interface BooleanList : PrimitiveList, Iterable<Boolean> {
    /**
     * Returns the specified element.
     * @throws IndexOutOfBoundsException the element at the specified index does not exist
     */
    public operator fun get(index: Int): Boolean
}

/**
 * A list of unboxed integers.
 */
public interface IntList : PrimitiveList, Iterable<Int> {
    /**
     * Returns the specified element.
     * @throws IndexOutOfBoundsException the element at the specified index does not exist
     */
    public operator fun get(index: Int): Int
}

/**
 * A list of unboxed long integers.
 */
public interface LongList : PrimitiveList, Iterable<Long> {
    /**
     * Returns the specified element.
     * @throws IndexOutOfBoundsException the element at the specified index does not exist
     */
    public operator fun get(index: Int): Long
}

/**
 * A list of unboxed floating-point numbers.
 */
public interface DoubleList : PrimitiveList, Iterable<Double> {
    /**
     * Returns the specified element.
     * @throws IndexOutOfBoundsException the element at the specified index does not exist
     */
    public operator fun get(index: Int): Double
}

// ------------------------------ mutable lists ------------------------------

/**
 * A mutable list of unboxed boolean values.
 */
public class MutableBooleanList : BooleanList {
    /**
     * The last element in this list.
     * @throws IllegalStateException stack is empty
     */
    public var last: Boolean
        get() {
            check(size != 0) { "List is empty" }
            return buffer[size - 1]
        }
        set(value) {
            check(size != 0) { "List is empty" }
            buffer[size - 1] = value
        }

    private var buffer: BooleanArray = BooleanArray(DEFAULT_SIZE)

    override var size: Int = 0

    /**
     * Pops the last element from this list.
     * @return the former last element in this list
     * @throws IllegalStateException stack is empty
     */
    public fun removeLast(): Boolean = last.also { --size }

    /**
     * Pushes [element] to the end of this list.
     */
    public operator fun plusAssign(element: Boolean) {
        if (size == buffer.size) {
            val new = BooleanArray(size * GROWTH_FACTOR)
            buffer.copyInto(new)
            buffer = new
        }
        buffer[size++] = element
    }

    override fun get(index: Int): Boolean = buffer[index]

    override fun iterator(): BooleanIterator = @KoverExclude object : BooleanIterator() {
        var cursor = 0

        override fun hasNext() = cursor < size
        override fun nextBoolean() = buffer[cursor++]
    }

    override fun toString(): String = buffer.asSequence().take(size).joinToString(prefix = "[", postfix = "]")
}

/**
 * A mutable list of unboxed integers.
 */
public class MutableIntList : IntList {
    /**
     * The last element in this list.
     * @throws IllegalStateException stack is empty
     */
    public var last: Int
        get() {
            check(size != 0) { "List is empty" }
            return buffer[size - 1]
        }
        set(value) {
            check(size != 0) { "List is empty" }
            buffer[size - 1] = value
        }

    private var buffer: IntArray = IntArray(DEFAULT_SIZE)

    override var size: Int = 0

    /**
     * Pops the last element from this list.
     * @return the former last element in this list
     * @throws IllegalStateException stack is empty
     */
    public fun removeLast(): Int = last.also { --size }

    /**
     * Pushes [element] to the end of this list.
     */
    public operator fun plusAssign(element: Int) {
        if (size == buffer.size) {
            val new = IntArray(size * GROWTH_FACTOR)
            buffer.copyInto(new)
            buffer = new
        }
        buffer[size++] = element
    }

    override fun get(index: Int): Int = buffer[index]

    override fun iterator(): IntIterator = @KoverExclude object : IntIterator() {
        var cursor = 0

        override fun hasNext() = cursor < size
        override fun nextInt() = buffer[cursor++]
    }

    override fun toString(): String = buffer.asSequence().take(size).joinToString(prefix = "[", postfix = "]")
}

/**
 * A mutable list of unboxed long integers.
 */
public class MutableLongList : LongList {
    /**
     * The last element in this list.
     * @throws IllegalStateException stack is empty
     */
    public var last: Long
        get() {
            check(size != 0) { "List is empty" }
            return buffer[size - 1]
        }
        set(value) {
            check(size != 0) { "List is empty" }
            buffer[size - 1] = value
        }

    private var buffer: LongArray = LongArray(DEFAULT_SIZE)

    override var size: Int = 0

    /**
     * Pops the last element from this list.
     * @return the former last element in this list
     * @throws IllegalStateException stack is empty
     */
    public fun removeLast(): Long = last.also { --size }

    /**
     * Pushes [element] to the end of this list.
     */
    public operator fun plusAssign(element: Long) {
        if (size == buffer.size) {
            val new = LongArray(size * GROWTH_FACTOR)
            buffer.copyInto(new)
            buffer = new
        }
        buffer[size++] = element
    }

    override fun get(index: Int): Long = buffer[index]

    override fun iterator(): LongIterator = @KoverExclude object : LongIterator() {
        var cursor = 0

        override fun hasNext() = cursor < size
        override fun nextLong() = buffer[cursor++]
    }

    override fun toString(): String = buffer.asSequence().take(size).joinToString(prefix = "[", postfix = "]")
}

/**
 * A mutable list of unboxed integers.
 */
public class MutableDoubleList : DoubleList {
    /**
     * The last element in this list.
     * @throws IllegalStateException stack is empty
     */
    public var last: Double
        get() {
            check(size != 0) { "List is empty" }
            return buffer[size - 1]
        }
        set(value) {
            check(size != 0) { "List is empty" }
            buffer[size - 1] = value
        }

    private var buffer: DoubleArray = DoubleArray(DEFAULT_SIZE)

    override var size: Int = 0

    /**
     * Pops the last element from this list.
     * @return the former last element in this list
     * @throws IllegalStateException stack is empty
     */
    public fun removeLast(): Double = last.also { --size }

    /**
     * Pushes [element] to the end of this list.
     */
    public operator fun plusAssign(element: Double) {
        if (size == buffer.size) {
            val new = DoubleArray(size * GROWTH_FACTOR)
            buffer.copyInto(new)
            buffer = new
        }
        buffer[size++] = element
    }

    override fun get(index: Int): Double = buffer[index]

    override fun iterator(): DoubleIterator = @KoverExclude object : DoubleIterator() {
        var cursor = 0

        override fun hasNext() = cursor < size
        override fun nextDouble() = buffer[cursor++]
    }

    override fun toString(): String = buffer.asSequence().take(size).joinToString(prefix = "[", postfix = "]")
}