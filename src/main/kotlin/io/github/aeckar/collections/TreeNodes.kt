package io.github.aeckar.collections

import io.github.aeckar.utils.KoverExclude
import kotlin.experimental.ExperimentalTypeInference

// ------------------------------ factories ------------------------------

/**
 * Returns a mutable tree node containing the given element.
 */
public fun <E> treeNodeOf(element: E): DataTreeNode<E> = DataTreeNode(element)

/**
 * Returns a read-only view of a tree whose nodes contain the given elements.
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildTree(
    rootElement: E,
    @BuilderInference builder: DataTreeNode<E>.() -> Unit
): ReadOnlyDataTreeNode<E> {
    return DataTreeNode(rootElement).apply(builder).readOnly()
}

// ------------------------------ interfaces ------------------------------

/**
 * A node in some larger, singly-linked tree.
 *
 * Unlike in Java, this library does not provide a dedicated tree class.
 * Instead, tree nodes are operated on directly.
 *
 * Instances may contain child nodes, but do not contain a reference to their parent.
 *
 * ```kotlin
 *     val tree = buildTree("5") { // Values indicate iteration order
 *         this += "0"
 *         this += "3".."2".."1"
 *         this += "4"
 *     }
 *     println(tree)               // 4 (root node)
 *     println(tree.treeString())  // 5
 *                                 // ├── 0
 *                                 // ├── 3
 *                                 // │   └── 2
 *                                 // │       └── 1
 *                                 // └── 4
 * ```
 */
public interface TreeNode<Self : TreeNode<Self>> : Node<Self> {
    /**
     * The child nodes of this one, or an empty list if none exist.
     *
     * The default implementation of this property returns the empty list.
     */
    public val children: List<Self> get() = listOf()

    /**
     * Contains the specific characters used to create the [treeString] of a node.
     */
    public data class Style(val vertical: Char, val horizontal: Char, val turnstile: Char, val corner: Char) {
        /**
         * Returns a line map containing the given characters.
         * @throws IllegalArgumentException [chars] does not contain exactly 4 characters
         */
        public constructor(chars: String) : this(chars[0], chars[1], chars[2], chars[3]) {
            require(chars.length == 4) { "String '$chars' must have 4 characters'" }
        }
    }

    /**
     * Returns a multi-line string containing the entire tree whose root is this node.
     */
    public fun treeString(lines: Style = UTF_8): String {
        val builder = StringBuilder()
        appendSubtree(builder, lines, branches = MutableBooleanList())
        builder.deleteAt(builder.lastIndex) // Remove trailing newline
        return builder.toString()
    }

    private fun appendSubtree(
        builder: StringBuilder,
        lines: Style,
        branches: MutableBooleanList
    ): Unit = with(builder) {
        fun prefixWith(corner: Char, lines: Style, branches: MutableBooleanList) {
            branches.forEach { append(if (it) "${lines.vertical}   " else "    ") }
            append(corner)
            append(lines.horizontal)
            append(lines.horizontal)
            append(' ')
        }

        append(nodeString())
        append('\n')
        if (children.isNotEmpty()) {
            children.asSequence().take(children.size.coerceAtLeast(1) - 1).forEach {
                prefixWith(lines.turnstile, lines, branches)
                branches += true
                it.appendSubtree(builder, lines, branches)
                branches.removeLast()
            }
            prefixWith(lines.corner, lines, branches)
            branches += false
            children.last().appendSubtree(builder, lines, branches)
            branches.removeLast()
        }
    }

    /**
     * Returns a multi-line string containing the entire tree whose root is this node.
     *
     * Behaves identically to [treeString] with default arguments.
     */
    override fun linkedString(): String = treeString()

    /**
     * Returns an iterator over the abstract syntax tree whose root is this node
     * in a bottom-up, left-to-right fashion (post-order).
     *
     * The first node returned is the bottom-left-most and the last node returned is this one.
     */
    override fun iterator(): Iterator<Self> = @Suppress("UNCHECKED_CAST") object : Iterator<Self> {
        private var cursor = this@TreeNode as Self
        private val parentStack = mutableListOf<Self>()
        private val childIndices = MutableIntList()

        init {
            childIndices += 0   // Prevent underflow in loop condition
            while (cursor.children.isNotEmpty()) {  // Move to bottom-left node
                parentStack.add(cursor)
                cursor = cursor.children.first()
                childIndices += 0
            }
        }

        override fun hasNext() = cursor !== this@TreeNode

        override fun next(): Self {
            cursor = parentStack.removeLast()
            childIndices.removeLast()
            while (childIndices.last <= cursor.children.lastIndex) {
                parentStack.add(cursor)
                cursor = cursor.children[childIndices.last]
                ++childIndices.last
                childIndices += 0
            }
            return cursor
        }
    }

    /**
     * Returns a string describing the state contained by this tree node.
     */
    override fun toString(): String

    public companion object {
        /**
         * Can be passed to [treeString] so that the lines in the returned string are made of UTF-8 characters.
         */
        public val UTF_8: Style = Style("│─├└")

        /**
         * Can be passed to [treeString] so that the lines in the returned string are made of ASCII characters.
         */
        public val ASCII: Style = Style("|-++")
    }
}

/**
 * A tree node whose children can be modified.
 */
public interface MutableTreeNode<Self : MutableTreeNode<Self>> : TreeNode<Self> {
    override val children: MutableList<Self>

    /**
     * Appends the given element as a tree node to this one.
     * @return this node
     */
    @KoverExclude
    public operator fun plus(node: Self): MutableTreeNode<Self> {
        return this.also { children.add(node) }
    }

    /**
     * Appends the given element as a tree node to this one.
     */
    public operator fun plusAssign(node: Self) {
        children.add(node)
    }
}

// ------------------------------ implementation ------------------------------

/**
 * A value-containing tree node whose children can be modified.
 */
public class DataTreeNode<E> @PublishedApi internal constructor(
    override val element: E
) : MutableTreeNode<DataTreeNode<@UnsafeVariance E>>, DataNode<DataTreeNode<@UnsafeVariance E>, E> {
    override val children: MutableList<DataTreeNode<@UnsafeVariance E>> = mutableListOf()

    /**
     * Appends the given element, as a tree node, to this one.
     * @return this node
     */
    public operator fun plus(value: E): DataTreeNode<E> {
        return this.also { children.add(DataTreeNode(value)) }
    }

    /**
     * Appends the given element, as a tree node, to this one.
     */
    public operator fun plusAssign(value: E) {
        children.add(DataTreeNode(value))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DataTreeNode<*>) {
            return false
        }
        return element == other.element
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String = "{ $element }"
}