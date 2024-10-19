package io.github.aeckar.iteration

/*
    PositionalIterator                  abstract class
        PositionalRevertibleIterator    abstract class
 */

/**
 * A basic iterator containing the current position, starting from 0.
 * @param position the position of the element being pointed to by the iterator.
 */
public abstract class PositionalIterator<out T>(
    protected var position: Int = 0
) : Iterator<T>