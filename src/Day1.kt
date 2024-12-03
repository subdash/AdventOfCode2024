import java.io.File
import java.io.InputStream
import kotlin.math.abs

const val PATH_TO_INPUT = "resources/puzzle_input.txt"

fun day1(): Long {
    val inputStream: InputStream = File(PATH_TO_INPUT).inputStream()
    val lineList = mutableListOf<String>()
    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()

    inputStream.bufferedReader().forEachLine { lineList.add(it) }
    lineList.forEach {
        val split = it.split("   ")
        left.add(Integer.parseInt(split.first()))
        right.add(Integer.parseInt(split.last()))
    }

    left.sort()
    right.sort()

    var distance: Long = 0

    for (i in left.indices) {
        distance += abs(left[i] - right[i])
    }

    return distance
}