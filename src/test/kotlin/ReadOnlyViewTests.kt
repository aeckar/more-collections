import io.github.aeckar.collections.readOnly
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ReadOnlyViewTests {
    @Test
    fun read_only_traversal() {
        val elements = listOf(1, "2", 3.0, 4L, 5.0F)
        assertContentEquals(
            expected = elements,
            actual = elements.readOnly()
        )
        assertContentEquals(
            expected = elements.subList(1, 3),
            actual = elements.readOnly().subList(1, 3)
        )
    }
}