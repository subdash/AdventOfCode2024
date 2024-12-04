import java.io.File
import java.io.InputStream
import kotlin.math.abs

private const val PATH_TO_INPUT = "resources/puzzle_input_day2.txt"

fun day2(): Pair<Int, Int> {
    val inputStream: InputStream = File(PATH_TO_INPUT).inputStream()
    val lineList = mutableListOf<String>()
    inputStream.bufferedReader().forEachLine { lineList.add(it) }

    val numLineList = lineList.map { line -> line.split(" ").map { it.toInt() } }
    val part1 = numLineList.fold(0) { acc, line -> acc + isSafe(line, problemDampener = false, retry = false) }
    val part2 = numLineList.fold(0) { acc, line -> acc + isSafe(line, problemDampener = true, retry = false) }

    return Pair(part1, part2)
}

private fun isSafe(numbers: List<Int>, problemDampener: Boolean, retry: Boolean): Int {
    val ascending = numbers[0] < numbers[1]

    for (i in 1..<numbers.size) {
        val prev = numbers[i - 1]
        val curr = numbers[i]
            // Check if same value
        if (prev == curr ||
            // Check if difference between prev and curr is greater than 3
            abs(prev - curr) > 3 ||
            // Check if strictly increasing or decreasing
            ascending && curr < prev ||
            !ascending && curr > prev
            ) {
            // We can apply the logic for part 1 up to here
            if (problemDampener && !retry) {
                // Try all permutations with one level removed
                for (j in numbers.indices) {
                    val newNumbers = mutableListOf<Int>()
                    for (k in numbers.indices) {
                        if (k != j) newNumbers.add(numbers[k])
                    }
                    // Check if that permutation is safe -- if so, we can say that this report is safe
                    if (isSafe(newNumbers, problemDampener = true, retry = true) == 1) {
                        return 1
                    }
                }
                // If no permutations of the report with 1 level removed are safe, then we have to return 0.
                return 0
            } else {
                // For part 1, we skip the entire branch above
                return 0
            }
        }
    }
    return 1
}
