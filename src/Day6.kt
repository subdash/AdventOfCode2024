import java.io.BufferedReader
import java.io.File
import java.util.*

private const val PATH_TO_INPUT = "resources/puzzle_input_day6.txt"

data class Position(var row: Int, var col: Int, var direction: Direction)

enum class Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN
}

private val getNextDirection = { direction: Direction ->
    when (direction) {
        Direction.LEFT -> Direction.UP
        Direction.RIGHT -> Direction.DOWN
        Direction.UP -> Direction.RIGHT
        Direction.DOWN -> Direction.LEFT
    }
}

private val getDelta = { direction: Direction ->
    when (direction) {
        Direction.LEFT -> Pair(0, -1)
        Direction.RIGHT -> Pair(0, 1)
        Direction.UP -> Pair(-1, 0)
        Direction.DOWN -> Pair(1, 0)
    }
}

private val hashPosition = { pos: Position, m: Int -> (pos.row * m) + pos.col }
private val unhashPosition = { n: Int, pos: Int ->
    val row = pos / n
    val col = pos - (row * n)
    Pair(row, col)
}
private val inBounds = { pos: Position, m: Int, n: Int ->
    pos.row >= 0 &&
            pos.col >= 0 &&
            pos.row < m &&
            pos.col < n
}

private data class State(
    val visited: MutableSet<Int>,
    val m: Int,
    val n: Int,
    val grid: Array<Array<Char>>,
    val start: Pair<Int, Int>,
    val position: Position
)

fun day6(): Pair<Int, Int> {
    val reader: BufferedReader = File(PATH_TO_INPUT).inputStream().bufferedReader()
    val lines = reader.readLines()
    // Set up the grid
    val m = lines.size
    val n = lines[0].length
    val grid = Array(m) { Array(n) { '0' } }
    var start = Pair(0, 0)
    for (row in 0..<m) {
        for (col in 0..<n) {
            grid[row][col] = lines[row][col]
            if (grid[row][col] == '^') {
                start = Pair(row, col)
            }
        }
    }
    // We'll keep track of all visited cells
    val visited: MutableSet<Int> = HashSet()
    val position = Position(start.first, start.second, Direction.UP)
    val state = State(visited, m, n, grid, start, position)
    val part1 = day6Pt1(state)
    // Reset state
    state.position.row = start.first
    state.position.col = start.second
    state.position.direction = Direction.UP
    val part2 = day6Pt2(state)

    return Pair(part1, part2)
}

private fun day6Pt1(state: State): Int {
    while (inBounds(state.position, state.m, state.n)) {
        state.visited.add(hashPosition(state.position, state.m))
        val delta = getDelta(state.position.direction)
        // Try to move to next cell
        val nextCell = Position(state.position.row + delta.first, state.position.col + delta.second, state.position.direction)
        if (!inBounds(nextCell, state.m, state.n)) {
            // We've exited the maze
            break
        }
        if (state.grid[nextCell.row][nextCell.col] == '#') {
            // We've hit an obstacle and must change direction
            state.position.direction = getNextDirection(state.position.direction)
        } else {
            state.position.apply {
                this.row = nextCell.row
                this.col = nextCell.col
            }
        }
    }

    return state.visited.size
}

private fun day6Pt2(state: State): Int {
    var positions = 0
    val hashedStartPosition = hashPosition(Position(state.start.first, state.start.second, Direction.UP), state.m)
    state.visited.remove(hashedStartPosition)
    for (i in state.visited) {
        val unhashedPosition = unhashPosition(state.n, i)
        val gridCopy = Array(state.m) { Array(state.n) { '0' } }

        for (row in 0..<state.m) {
            for (col in 0..<state.n) {
                gridCopy[row][col] = state.grid[row][col]
            }
        }

        gridCopy[unhashedPosition.first][unhashedPosition.second] = '#'
        state.position.row = state.start.first
        state.position.col = state.start.second
        state.position.direction = Direction.UP
        val timesVisited = Array(state.m * state.n) { 0 }

        while (inBounds(state.position, state.m, state.n)) {
            val hashedPosition = hashPosition(state.position, state.m)
            timesVisited[hashedPosition]++
            // This is a really shitty way of determining if we are stuck in the maze. A better way would be to
            // track position and direction in the visited table. If we've already been to this cell facing this
            // direction, then we're stuck.
            if (timesVisited[hashedPosition] > 4) {
                ++positions
                break
            }
            val delta = getDelta(state.position.direction)
            // Try to move to next cell
            val nextCell = Position(state.position.row + delta.first, state.position.col + delta.second, state.position.direction)
            if (!inBounds(nextCell, state.m, state.n)) {
                // We've exited the maze
                break
            }
            if (gridCopy[nextCell.row][nextCell.col] == '#') {
                // We've hit an obstacle and must change direction
                state.position.direction = getNextDirection(state.position.direction)
            } else {
                state.position.apply {
                    this.row = nextCell.row
                    this.col = nextCell.col
                }
            }
        }
    }
    return positions
}