import java.io.BufferedReader
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

private const val PATH_TO_INPUT = "resources/puzzle_input_day4.txt"

data class QueueNode(
    val row: Int,
    val col: Int,
    val letter: Char,
    val delta: Pair<Int, Int>
)

private val directions = listOf(
    Pair(0, 1), // right
    Pair(0, -1), // left
    Pair(1, 0), // down
    Pair(-1, 0), // up
    Pair(-1, -1), // up/left
    Pair(1, -1), // down/left
    Pair(-1, 1), // up/right
    Pair(1, 1), // down/right
)

fun day4(): Pair<Int, Int> {
    val reader: BufferedReader = File(PATH_TO_INPUT).inputStream().bufferedReader()
    val content = reader.readLines()
    // Convert rows of text to a character matrix
    val matrix = content.map { line -> line.toCharArray() }
    return Pair(day4Pt1(matrix), day4Pt2(matrix))
}

private fun day4Pt1(matrix: List<CharArray>): Int {
    // We'll use a queue to BFS each X we find
    val xs: Queue<QueueNode> = LinkedList()
    for (i in 0..<matrix.size) {
        for (j in 0..<matrix[0].size) {
            if (matrix[i][j] == 'X') {
                // For every X we see, add 8 queue node, one for each direction
                for (d in directions) {
                    xs.offer(QueueNode(i, j, 'X', d))
                }
            }
        }
    }
    val isValid = { x: Int, y: Int -> x >= 0 && y >= 0 && x < matrix.size && y < matrix[0].size }
    var tally = 0
    // Here's where the breadth first search starts
    while (xs.isNotEmpty()) {
        val curr = xs.poll()
        // If the expected letter does not equal the current letter pointed to, dip out
        if (matrix[curr.row][curr.col] != curr.letter) {
            continue
        }
        // If the expected letter is S and that is the letter pointed to (validated above), then we increase the tally.
        if (curr.letter == 'S') {
            ++tally
            continue
        }
        // Otherwise, we need to search for the rest of the letters in the word
        val nextLetter = when(curr.letter) {
            'X' -> 'M'
            'M' -> 'A'
            'A' -> 'S'
            else -> throw Error("Unexpected letter")
        }
        // Use delta from queue node to determine next cell
        val nextRow = curr.row + curr.delta.first
        val nextCol = curr.col + curr.delta.second
        if (isValid(nextRow, nextCol)) {
            // If that cell is in-bounds, put it on the queue
            xs.offer(QueueNode(nextRow, nextCol, nextLetter, curr.delta))
        }
    }
    return tally
}

private fun day4Pt2(matrix: List<CharArray>): Int {
    // We'll use a queue to BFS each X we find
    val aCells: MutableList<Pair<Int, Int>> = ArrayList()
    // Skip first and last rows/columns -- we can't form an X from an A found there
    for (i in 1..<matrix.size - 1) {
        for (j in 1..<matrix[0].size - 1) {
            // Find every A, add to list
            if (matrix[i][j] == 'A') {
                aCells.add(Pair(i, j))
            }
        }
    }

    // Tally up every A that makes an X
    return aCells.fold(0) { acc, pair -> acc + isX(matrix, pair)}
}

private fun isX(matrix: List<CharArray>, cell: Pair<Int, Int>): Int {
    val row = cell.first
    val col = cell.second
    val x1A = Pair(row - 1, col - 1) // Top left
    val x1B = Pair(row + 1, col + 1) // Bottom right
    val x2A = Pair(row + 1, col - 1) // Bottom left
    val x2B = Pair(row - 1, col + 1) // Top right

    var first = false
    var second = false

    // Check if top left/bottom right form MAS
    if ((matrix[x1A.first][x1A.second] == 'M' && matrix[x1B.first][x1B.second] == 'S') ||
        (matrix[x1A.first][x1A.second] == 'S' && matrix[x1B.first][x1B.second] == 'M')) {
        first = true
    }
    // Check if top right/bottom left form MAS
    if ((matrix[x2A.first][x2A.second] == 'M' && matrix[x2B.first][x2B.second] == 'S') ||
        (matrix[x2A.first][x2A.second] == 'S' && matrix[x2B.first][x2B.second] == 'M')) {
        second = true
    }
    if (first && second) return 1

    return 0
}


