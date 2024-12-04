import java.io.File
import java.io.InputStream
import java.util.PriorityQueue
import kotlin.math.abs

private const val PATH_TO_INPUT = "resources/puzzle_input_day1.txt"

fun day1(): Long {
    val inputStream: InputStream = File(PATH_TO_INPUT).inputStream()
    val lineList = mutableListOf<String>()
    val left = PriorityQueue<Int>()
    val right = PriorityQueue<Int>()

    inputStream.bufferedReader().forEachLine { lineList.add(it) }
    lineList.forEach {
        val split = it.split("   ")
        left.add(Integer.parseInt(split.first()))
        right.add(Integer.parseInt(split.last()))
    }

    var distance: Long = 0

    while (left.isNotEmpty()) {
        distance += abs(left.poll().toLong() - right.poll().toLong())
    }

    return distance
}