import io.github.aeckar.collections.*
import kotlin.collections.toList
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ListNodeTests {
    @Test
    fun get_or_insert_pivot() {
        val initial = DataPivot(1, "head")
        check(initial.getOrInsert(1) { "first" } === initial)
        initial.apply {
            getOrInsert(-8) { "negative eighth" }
            getOrInsert(2) { "second" }
            getOrInsert(17) { "seventeenth" }
            getOrInsert(5) { "fifth" }
            getOrInsert(20) { "twentieth" }
        }
        assertContentEquals(
            expected = listOf(-8, 1, 2, 5, 17, 20),
            actual = initial.head().toList().map { it.position }
        )
    }

    @Test
    fun append_node() {
        val head = listNodeOf(0).apply { insertAfter(1) }
        head.next().insertAfter(2)
        assertEquals(head.next().element, 1)
        assertEquals(head.next().next().element, 2)
        head.insertAfter(-1)
        assertContentEquals(
            expected = listOf(0, -1, 1, 2),
            actual = head.next().toArrayList().map { it.element }   // Position unimportant when using `toArrayList`
        )
    }

    @Test
    fun prepend_node() {
        val tail = listNodeOf(0).apply { insertBefore(1) }
        tail.last().insertBefore(2)
        assertEquals(tail.last().element, 1)
        assertEquals(tail.last().last().element, 2)
        tail.insertBefore(-1)
        assertContentEquals(
            expected = listOf(0, -1, 1, 2),
            actual = tail.downToHead().map { it.element }
        )
    }

    @Test
    fun forward_list_traversal() {
        val linkedList = headOf(0, 1, 2)
        for ((index, node) in linkedList.withIndex()) {
            assertEquals(node.element, index)
        }
    }

    @Test
    fun forward_traversal() {
        val linkedList = listNodeOf(0).apply { insertAfter(1) }
        linkedList.next().insertAfter(2)
        for ((index, node) in linkedList.withIndex()) {
            assertEquals(node.element, index)
        }
    }

    @Test
    fun backward_traversal() {
        val linkedList = listNodeOf(2).apply { insertAfter(1) }
        linkedList.next().insertAfter(0)
        for ((index, node) in linkedList.tail().downToHead().withIndex()) {
            assertEquals(node.element, index)
        }
    }

    @Test
    fun forward_search_or_last() {
        headPivotOf(1 to "first", 2 to "second", 3 to "third", 4 to "fourth").head().apply {
            assertEquals("second", seek { it.position == 2 }.value)
            assertEquals("third", seek { it.position == 3 }.value)
            assertEquals("fourth", seek { false }.value)    // Same as call to `tail`
        }

    }

    @Test
    fun backward_search_or_first() {
        headPivotOf(1 to "first", 2 to "second", 3 to "third", 4 to "fourth").tail().apply {
            assertEquals("third", backtrace { it.position == 3 }.value)
            assertEquals("second", backtrace { it.position == 2 }.value)
            assertEquals("first", backtrace { false }.value)    // Same as call to `head`
        }
    }

    @Test
    fun conversion_to_list() {
        assertContentEquals(
            expected = listOf(0, 1, 2),
            actual = headOf(0, 1, 2).toList().map { it.element }
        )
    }
}