import io.github.aeckar.collections.buildMultiSet
import io.github.aeckar.collections.emptyMultiSet
import io.github.aeckar.collections.multiSetOf
import io.github.aeckar.collections.mutableMultiSetOf
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlin.test.fail

class MultiSetTests {
    @Test
    fun set_behavior() {
        val elements = arrayOf(0, 1L, 2.0, "three")
        val list = listOf(*elements)
        val immutable = buildMultiSet { addAll(list) }
        check(immutable.containsAll(list))
        val mutable = mutableMultiSetOf(*elements)
        check(mutable.remove(elements[Random(0).nextInt(0..3)]))
        check(mutable.size == 3)
    }

    @Test
    fun instance_counting_behavior() {
        val rand = Random(0)
        val elements = mutableListOf<Int>()
        val set = mutableMultiSetOf<Int>()
        repeat(rand.nextInt(until = 10)) {
            val element = rand.nextInt()
            repeat(rand.nextInt(until = 10)) {
                elements += element
                set += element
            }
        }
        for (element in elements.distinct()) {
            check(set.count(element) == elements.count { it == element })
        }
    }

    @Test
    fun empty_set() {
        val empty = emptyMultiSet<Int>()
        check(empty.size == 0)
        check(empty.none())
        for (element in empty.iterator()) {
            fail("Multi-set is not empty")
        }
    }

    @Test
    fun partial_removal() {
        val elements = mutableMultiSetOf(1,2,3,1)
        check(elements.count(1) == 2)
        elements.remove(1)
        check(elements.count(1) == 1)
        elements.remove(1)
        check(elements.count(1) == 0)
    }
}