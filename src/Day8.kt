import java.io.BufferedReader
import java.io.File
import java.util.ArrayList
import java.util.HashSet
import kotlin.math.abs

private const val PATH_TO_INPUT = "resources/puzzle_input_day8.txt"

private data class Cell(var row: Int, var col: Int)

enum class LeftOrRight {
    LEFT,
    RIGHT,
}

fun day8(): Pair<Int, Int> {
    val reader: BufferedReader = File(PATH_TO_INPUT).inputStream().bufferedReader()
    val grid = reader.readLines().map(String::toCharArray)

    return Pair(day8Pt1(grid), day8Pt2(grid))
}

private fun day8Pt1(grid: List<CharArray>): Int {
    val antinodePositions = HashSet<Int>()
    val antennaPositions = HashMap<Char, MutableList<Cell>>()
    val m = grid.size
    val n = grid[0].size
    for (row in 0..<m) {
        for (col in 0..<n) {
            val curr = grid[row][col]
            if (curr != '.') {
                antennaPositions
                    .computeIfAbsent(curr) { _ -> ArrayList() }
                    .add(Cell(row, col))
            }
        }
    }

    for (entry in antennaPositions.entries) {
        val nEntries = entry.value.size
        if (nEntries == 1) continue // Skip -- no antinodes if there is only one antenna of that kind
        for (i in 0..<nEntries) {
            for (j in 0..<nEntries) {
                if (i == j) continue

                val first = entry.value[i]
                val second = entry.value[j]
                val top = if (first.row < second.row) first else second
                val bottom = if (first.row < second.row) second else first
                val rowDelta = abs(top.row - bottom.row)
                val colDelta = abs(top.col - bottom.col)
                val topDirection = if (top.col < bottom.col) LeftOrRight.LEFT else LeftOrRight.RIGHT
                val bottomDirection = if (bottom.col < top.col) LeftOrRight.LEFT else LeftOrRight.RIGHT
                val topColDelta = if (topDirection == LeftOrRight.LEFT) -colDelta else colDelta
                val bottomColDelta = if (bottomDirection == LeftOrRight.LEFT) -colDelta else colDelta

                val antinode1 = Cell(top.row - rowDelta, top.col + topColDelta)
                val antinode2 = Cell(bottom.row + rowDelta, bottom.col + bottomColDelta)

                if (inBounds(antinode1, m, n)) {
                    antinodePositions.add(normalize(antinode1, n))
                }
                if (inBounds(antinode2, m, n)) {
                    antinodePositions.add(normalize(antinode2, n))
                }
            }
        }
    }

    return antinodePositions.size
}

private fun day8Pt2(grid: List<CharArray>): Int {
    val antinodePositions = HashSet<Int>()
    val antennaPositions = HashMap<Char, MutableList<Cell>>()
    val m = grid.size
    val n = grid[0].size
    for (row in 0..<m) {
        for (col in 0..<n) {
            val curr = grid[row][col]
            if (curr != '.') {
                antennaPositions
                    .computeIfAbsent(curr) { _ -> ArrayList() }
                    .add(Cell(row, col))
            }
        }
    }

    for (entry in antennaPositions.entries) {
        val nEntries = entry.value.size
        if (nEntries == 1) continue // Skip -- no antinodes if there is only one antenna of that kind
        for (i in 0..<nEntries) {
            for (j in 0..<nEntries) {
                if (i == j) continue

                val first = entry.value[i]
                val second = entry.value[j]
                val top = if (first.row < second.row) first else second
                val bottom = if (first.row < second.row) second else first
                val rowDelta = abs(top.row - bottom.row)
                val colDelta = abs(top.col - bottom.col)
                val topDirection = if (top.col < bottom.col) LeftOrRight.LEFT else LeftOrRight.RIGHT
                val bottomDirection = if (bottom.col < top.col) LeftOrRight.LEFT else LeftOrRight.RIGHT
                val topColDelta = if (topDirection == LeftOrRight.LEFT) -colDelta else colDelta
                val bottomColDelta = if (bottomDirection == LeftOrRight.LEFT) -colDelta else colDelta

                /*
                    This was easy to miss:
                    `This means that some of the new antinodes will occur at the position of each antenna
                    (unless that antenna is the only one of its frequency).`

                    So unlike pt1, we should count each antenna as an antinode (unless there is only one of that antenna).
                 */
                val antinode1 = top.copy()
                val antinode2 = bottom.copy()

                while (inBounds(antinode1, m, n)) {
                    antinodePositions.add(normalize(antinode1, n))
                    antinode1.row -= rowDelta
                    antinode1.col += topColDelta
                }
                while (inBounds(antinode2, m, n)) {
                    antinodePositions.add(normalize(antinode2, n))
                    antinode2.row += rowDelta
                    antinode2.col += bottomColDelta
                }
            }
        }
    }

    return antinodePositions.size
}

private fun normalize(pos: Cell, n: Int): Int = (pos.row * n) + pos.col

private fun inBounds(pos: Cell, m: Int, n: Int): Boolean =
    pos.row >= 0 && pos.col >= 0 && pos.row < m && pos.col < n
