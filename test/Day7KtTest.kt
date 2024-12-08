import org.junit.jupiter.api.Test

class Day7KtTest {

    @Test
    fun testSolve() {
        assert(solve(listOf(6, 3, 3)) == 6L) // 3 + 3 = 6
        assert(solve(listOf(9, 3, 3)) == 9L) // 3 * 3 = 9
        assert(solve(listOf(16, 2, 16, 2)) == 0L) // unsolvable
        assert(solve(listOf(13, 5, 5)) == 0L) // unsolvable
        assert(solve(listOf(123345, 123, 345), withCat = false) == 0L) // unsolvable
        assert(solve(listOf(123345, 123, 345), withCat = true) == 123345L) // solvable with concatenation
    }
}
