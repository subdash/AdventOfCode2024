import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.VisibleForTesting
import java.io.BufferedReader
import java.io.File
import java.util.Stack
import java.util.stream.Collectors

private const val PATH_TO_INPUT = "resources/puzzle_input_day7.txt"

sealed class Operator {

    data object ADD : Operator()
    data object MUL : Operator()
    data object CAT : Operator()
    companion object {
        fun values(): Array<Operator> {
            return arrayOf(ADD, MUL)
        }
        fun valuesWithConcat(): Array<Operator> {
            return values() + arrayOf(CAT)
        }
    }
}

fun day7(): Pair<Long, Long> {
    val reader: BufferedReader = File(PATH_TO_INPUT).inputStream().bufferedReader()
    val lines = reader.readLines().stream().map { line ->
        line.split(' ').map{ num -> num.filter { it in '0'..'9' }.toLong() }
    }.collect(Collectors.toList())
    return Pair(day7Pt1(lines), day7Pt2(lines))
}

private fun day7Pt1(lines: MutableList<List<Long>>): Long =
    runBlocking(Dispatchers.Default) {
        channelFlow {
            lines.forEach { line ->
                launch {
                    send(solve(line))
                }
            }
        }.toList()
    }.sum()

private fun day7Pt2(lines: MutableList<List<Long>>): Long =
    runBlocking(Dispatchers.Default) {
        channelFlow {
            lines.forEach { line ->
                launch {
                    send(solve(line, withCat = true))
                }
            }
        }.toList()
    }.sum()

@VisibleForTesting
fun solve(nums: List<Long>, operators: Stack<Operator> = Stack(), withCat: Boolean = false): Long {
    val target = nums.first()
    val operands = nums.slice(1..<nums.size)
    if (operators.size == nums.size - 2) {
        val result = applyOps(operands, operators)
        return if (result == target) {
            target
        } else {
            // Return anything but target to get out of infinite recursion
            target + 1
        }
    }

    for (operator in if (withCat) Operator.valuesWithConcat() else Operator.values()) {
        // Backtrack: add current operator in list, remove after recursive invocation,
        // then do that with the next operator. This will give us every permutation of
        // operators for the operands.
        operators.push(operator)
        val result = solve(nums, operators, withCat)
        if (result == target) {
            return target
        }
        operators.pop()
    }

    return 0
}

private fun applyOps(operands: List<Long>, operators: List<Operator>): Long {
    val concatenateNumbers = { x: Long, y: Long -> "$x$y".toLong() }
    if (operands.size == 1) return operands.first()
    val res = when (operators.first()) {
        Operator.ADD -> operands.first() + operands[1]
        Operator.MUL -> operands.first() * operands[1]
        Operator.CAT -> concatenateNumbers(operands.first(), operands[1])
    }
    return applyOps(listOf(res) + operands.slice(2..<operands.size), operators.slice(1..<operators.size))
}
