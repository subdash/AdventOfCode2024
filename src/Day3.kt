import java.io.BufferedReader
import java.io.File

private const val PATH_TO_INPUT = "resources/puzzle_input_day3.txt"

fun day3(): Pair<Int, Int> {
    val reader: BufferedReader = File(PATH_TO_INPUT).inputStream().bufferedReader()
    val content = reader.readLines().joinTo(StringBuilder()).toString()

    val first = process(content, false)
    val second = process(content, true)
    return Pair(first, second)
}

private fun process(line: String, handleDoDont: Boolean): Int {
    // Find first mul() instruction
    val firstOccurrenceMul = line.indexOf("mul(")
    val firstOccurrenceDont = line.slice(0..firstOccurrenceMul).indexOf("don't()")

    // (for part 2) We don't need to track state, we just check if there's a don't() before a mul(),
    // and if so we must skip to the next do().
    if (handleDoDont && firstOccurrenceDont != -1 && firstOccurrenceDont < firstOccurrenceMul) {
        val firstOccurrenceDo = line.slice(0..<line.length).indexOf("do()")
        // Handle end of input
        if (firstOccurrenceDo == -1) return 0;
        return process(line.slice(firstOccurrenceDo..<line.length), true)
    }

    // Go to first operand
    var currIdx = firstOccurrenceMul + 4
    // Handle a case where mul( is at the end
    if (currIdx >= line.length) return 0
    // Iterate over the following characters until we don't see a digit anymore
    var currCh = line[currIdx]
    val sb = StringBuilder()
    while (Character.isDigit(currCh)) {
        sb.append(currCh)
        currCh = line[++currIdx]
    }
    // If it's a valid instruction, the next char is a comma, otherwise, skip over this part of
    // the string by recursively calling this function on a slice of the rest of the string
    if (currCh != ',') return process(line.slice(currIdx..<line.length), handleDoDont)
    currCh = line[++currIdx]
    val firstOperand = Integer.parseInt(sb.toString())
    sb.clear()
    // Keep going, process the next number
    while (Character.isDigit(currCh)) {
        sb.append(currCh)
        currCh = line[++currIdx]
    }
    // It's only a valid instruction if digits are followed by closing parens
    if (currCh != ')') return process(line.slice(currIdx..<line.length), handleDoDont)
    val secondOperand = Integer.parseInt(sb.toString())
    // Multiply the operands together and add that result to a recursive call on the rest of the string
    return (firstOperand * secondOperand) + process(line.slice(currIdx..<line.length), handleDoDont)
}
