package io.github.aeckar.iteration

import io.github.aeckar.collections.MutableIntList
import io.github.aeckar.utils.KoverExclude
import io.github.aeckar.utils.orNoSuchElement
import java.io.Reader
import java.lang.System.lineSeparator

/*
    RevertibleIterator                  interface
        PivotIterator                   interface
            CharPivotIterator           interface
            AbstractPivotIterator       abstract class
        AbstractPivotIterator           abstract class
        CharRevertibleIterator          interface
            SourceRevertibleIterator    class
            CharPivotIterator           interface
            StringRevertibleIterator    class
        PositionalRevertibleIterator    abstract class
            ListRevertibleIterator      class
            StringRevertibleIterator    class
 */

// ------------------------------ factories ------------------------------

// Restrict visibility of implementations to private API

/**
 * Returns a revertible iterator over the elements in the list.
 */
public fun <E> List<E>.revertibleIterator(): RevertibleIterator<E, Int> = ListRevertibleIterator(this)

/**
 * Returns a revertible iterator over the characters in this string.
 */
public fun String.revertibleIterator(): CharRevertibleIterator<Int> = StringRevertibleIterator(this)

/**
 * Returns a revertible iterator over the characters in this source, loaded one line at a time.
 *
 * For any newline sequence, returns the newline character (`'\n'`).
 *
 * If the receiver is [closed][Reader.close],
 * any function called from the returned instance throws an [IllegalStateException].
 * Making this source buffered provides no performance benefit to the returned iterator.
 */
public fun Reader.revertibleIterator(): CharRevertibleIterator<SourcePosition> = SourceRevertibleIterator(this)

// ------------------------------------------------------------

/**
 * A sequence of elements whose position can be saved and reverted to later.
 *
 * ```kotlin
 *     val chars = "Hello, world!".revertibleIterator()
 *     chars.save()
 *     chars.advance(7)
 *     println(Iterable { chars }.joinToString(""))    // world!
 *     chars.revert()
 *     println(Iterable { chars }.joinToString(""))    // Hello, world!
 * ```
 */
public interface RevertibleIterator<out E, out P> : Iterator<E> {
    /**
     * Advances the cursor pointing to the current element by the given number of places.
     * @throws IllegalArgumentException [places] is negative
     */
    public fun advance(places: Int)

    /**
     * Saves the current cursor position.
     *
     * Can be called more than once to save multiple positions, and even if [isExhausted] is true.
     */
    public fun save()

    /**
     * Reverts the position of the cursor to the one that was last [saved][save],
     * and removes it from the set of saved positions.
     * @throws IllegalStateException [save] has not been called prior
     */
    public fun revert()

    /**
     * Removes the cursor position last [saved][save] without reverting the current cursor position to it.
     * @throws IllegalStateException [save] has not been called prior
     */
    public fun removeSave()

    /**
     * Returns the next element in the sequence without advancing the current cursor position.
     * @throws NoSuchElementException the iterator is exhausted
     */
    public fun peek(): E

    /**
     * Returns true if no more elements can be read from this iterator
     * without reverting to a previously saved cursor position.
     *
     * Equal in value to `!`[hasNext]`.
     */
    @KoverExclude
    public fun isExhausted(): Boolean = !hasNext()

    /**
     * Returns an object representing the current position of this iterator.
     */
    public fun position(): P

    /**
     * Returns true if [other] is a revertible iterator over the same instance at the same position as this one.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Returns a unique value representing the current position.
     */
    override fun hashCode(): Int

    /**
     * Returns a string containing a peek of the next element with positional information.
     */
    override fun toString(): String
}

/**
 * A revertible iterator over a sequence of characters.
 */
@KoverExclude   // Covered by tests on inheritors
public interface CharRevertibleIterator<out P> : RevertibleIterator<Char, P>, CharIterator {
    /**
     * Returns a [peek] of the next unboxed character.
     * @throws NoSuchElementException the iterator is exhausted
     */
    public fun peekChar(): Char

    @KoverExclude
    override fun peek(): Char = peekChar()
}

/**
 * A revertible iterator over an indexable sequence of elements.
 */
internal abstract class PositionalRevertibleIterator<out E> : PositionalIterator<E>(), RevertibleIterator<E, Int> {
    protected abstract val elements: Any?

    private val savedPositions = MutableIntList()

    final override fun advance(places: Int) {
        require(places >= 0) { "Cannot advance by negative amount" }
        position += places
    }

    final override fun save() {
        savedPositions += position
    }

    final override fun revert() {
        position = removeLastSave()
    }

    final override fun removeSave() {
        removeLastSave()
    }

    final override fun position() = position

    final override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is PositionalRevertibleIterator<*>) {
            return false
        }
        return elements === other.elements && position == other.position
    }

    final override fun hashCode(): Int {
        var result = elements.hashCode()
        result = 31 * result + position
        return result
    }

    final override fun toString(): String {
        val message = if (isExhausted()) "<past final position>" else "${peek()}"
        return "$message (index = $position)"
    }

    private fun removeLastSave(): Int {
        return try {
            savedPositions.removeLast()
        } catch (_: NoSuchElementException) {
            error("No positions saved")
        }
    }
}

internal class ListRevertibleIterator<out E>(override val elements: List<E>) : PositionalRevertibleIterator<E>() {
    override fun hasNext() = position < elements.size
    override fun isExhausted() = position >= elements.size
    override fun next(): E = peek().also { ++position }
    override fun peek() = orNoSuchElement { elements[position] }
}

internal class StringRevertibleIterator(
    override val elements: String
) : PositionalRevertibleIterator<Char>(), CharRevertibleIterator<Int> {
    override fun next() = nextChar()
    override fun hasNext() = position < elements.length
    override fun isExhausted() = position >= elements.length
    override fun nextChar() = peekChar().also { ++position }
    override fun peekChar() = orNoSuchElement { elements[position] }
}

internal class SourceRevertibleIterator(source: Reader) : CharRevertibleIterator<SourcePosition> {
    private val source = source.buffered()
    private val lines = mutableListOf("")   // Allow first-time bounds checking
    private val savedPositions = mutableListOf<SourcePosition>()
    private var line = 0
    private var column = 0

    override fun next(): Char = nextChar()
    override fun nextChar(): Char = peekChar().also { ++column }
    override fun hasNext(): Boolean = updatePosition()

    override fun peekChar(): Char {
        updatePositionOrThrow()
        val line = lines[line]
        return if (column == line.length) '\n' else line[column]
    }

    override fun advance(places: Int) {
        require(places >= 0) { "Cannot advance by negative amount" }
        column += places
    }

    override fun save() {
        savedPositions += position()
    }

    override fun revert() {
        val (line, column) = removeLastSave()
        this.line = line
        this.column = column
    }

    override fun removeSave() {
        removeLastSave()
    }

    override fun position(): SourcePosition {
        updatePositionOrThrow()
        return SourcePosition(line, column)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SourceRevertibleIterator) {
            return false
        }
        return source === other.source && line == other.line && column == other.column
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + line
        result = 31 * result + column
        return result
    }

    override fun toString(): String {
        val message = if (isExhausted()) "<past final position>" else "${peekChar()}"
        return "$message (line = $line, column = $column)"  // Line, column updated by isExhausted()
    }

    /**
     * Returns true if [hasNext].
     */
    private fun updatePosition(): Boolean {
        while (column >= lines[line].length) {
            if (line == lines.lastIndex) {  // Do not return newline in place of null terminator
                lines += source.readLine() ?: return false
            }
            if (column == lines[line].length) {
                break
            }
            column -= lines[line].lastIndex + lineSeparator().length
            ++line
        }
        if (line == 0) {    // Do not return newline in place of first character
            column += lineSeparator().lastIndex
        }
        return true
    }

    private fun updatePositionOrThrow() {
        !updatePosition() implies { throw NoSuchElementException("Iterator is past final position") }
    }

    private fun removeLastSave(): SourcePosition {
        return try {
            savedPositions.removeLast()
        } catch (_: NoSuchElementException) {
            error("No positions saved")
        }
    }
}