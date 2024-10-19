package io.github.aeckar.iteration

/**
 * Returns a removable object referring to the current object pointed to by this iterator.
 */
@JvmName("asMutableRemovable")
public fun MutableIterator<*>.asRemovable(): Removable = Removable { remove() }

/**
 * A sequence of characters providing unboxed access to each element.
 * @see kotlin.collections.CharIterator
 */
public interface CharIterator : Iterator<Char> {
    /**
     * Returns the [next] unboxed character.
     */
    public fun nextChar(): Char

    override fun next(): Char
}