package io.github.aeckar.collections

import io.github.aeckar.collections.TreeNode.Companion.toString
import io.github.aeckar.utils.KoverExclude

/*
    Containment of values not supported by base implementations/interfaces to allow for
    flexibility of API design (see io.github.aeckar.parsing.SyntaxTreeNode)
*/

/**
 * Returns the single element contained by this node.
 */
@KoverExclude
public operator fun <E> DataNode<*, E>.component1(): E = element

/**
 * A node of some larger, aggregate data structure.
 */
public interface Node<Self : Node<Self>> : Iterable<Self> {
    /**
     * Returns a string containing all components of the larger data structure, starting from this one.
     * @see nodeString
     */
    public fun linkedString(): String

    /**
     * Returns this node as a string component of the default implementation of [linkedString].
     *
     * By default, behaves identically to [toString].
     */
    @KoverExclude
    public fun nodeString(): String = toString()
}

/**
 * A node that contains a single element.
 */
public interface DataNode<Self : DataNode<Self, E>, out E> : Node<Self> {
    /**
     * The element contained by this node.
     */
    public val element: E

    override fun nodeString(): String = element.toString()
}