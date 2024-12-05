import java.io.BufferedReader
import java.io.File
import java.util.*
import kotlin.collections.HashMap

private const val PATH_TO_INPUT = "resources/puzzle_input_day5.txt"

fun day5(): Pair<Int, Int> {
    val reader: BufferedReader = File(PATH_TO_INPUT).inputStream().bufferedReader()
    val content = reader.readLines()
    val rules = HashMap<Int, MutableSet<Int>>()
    val updates = mutableListOf<MutableList<Int>>()
    for (line in content) {
        if (line.length < 2) continue
        if (line[2] == '|') {
            val split = line.split('|').map(Integer::parseInt)
            rules.computeIfAbsent(split[0], { mutableSetOf() }).add(split[1])
        } else {
            updates.add(line.split(',').map(Integer::parseInt).toMutableList())
        }
    }
    var tally = 0
    val incorrect = mutableListOf<MutableList<Int>>()
    for (update in updates) {
        val seen = TreeSet<Int>()
        var valid = true
        for (page in update) {
            seen.add(page)
            if (rules.containsKey(page)) {
                val mustBeAfter = rules[page]
                mustBeAfter?.iterator().run {
                    while (this?.hasNext() == true) {
                        if (seen.contains(this.next())) {
                            valid = false
                            break
                        }
                    }
                }
                if (!valid) break
            }
        }
        if (valid) {
            tally += update[update.size / 2]
        } else {
            incorrect.add(update)
        }
    }

    var incorrectTally = 0
    for (update in incorrect) {
        incorrectTally += reorder(update, rules)
    }

    return Pair(tally, incorrectTally)
}

private fun reorder(update: MutableList<Int>, rules: Map<Int, Set<Int>>): Int {
    val seen = TreeSet<Int>()
    for (i in 0..<update.size) {
        val page = update[i]
        seen.add(page)
        if (rules.containsKey(page)) {
            val mustBeAfter = rules[page]
            mustBeAfter?.iterator().run {
                while (this?.hasNext() == true) {
                    val mustBeAfterValue = this.next()
                    if (seen.contains(mustBeAfterValue)) {
                        val swapIndex = update.indexOf(mustBeAfterValue)
                        update[swapIndex] = page
                        update[i] = mustBeAfterValue
                        return reorder(update, rules)
                    }
                }
            }
        }
    }
    return update[update.size / 2]
}
