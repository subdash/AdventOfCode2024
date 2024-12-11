import java.io.BufferedReader
import java.io.File
import java.util.*

private const val PATH_TO_INPUT = "resources/puzzle_input_day9.txt"

fun day9(): Long {
    val reader: BufferedReader = File(PATH_TO_INPUT).inputStream().bufferedReader()
    // Take input, convert to char array, filter out zeroes
    val input = reader.readLine().toCharArray()
    val nums = ArrayList<Int>() // We'll track the numbers with values
    val spaces: Queue<Int> = LinkedList() // For spaces, we'll just track the indices
    var id = 0

    for (i in input.indices) {
        val curr: Int = input[i] - '0'
        // Even number indices get the number added
        if (i % 2 == 0) {
            // Take the number in the input, add the current id to nums that many times
            repeat(curr) {
                nums.add(id)
            }
            ++id
        } else {
            repeat(curr) {
                spaces.add(nums.size)
                nums.add(-1) // Placeholder for empty index
            }
        }
    }

    var endPtr = nums.size - 1
    var total = 0L

    while (spaces.isNotEmpty()) {
        while (nums[endPtr] == -1) {
            --endPtr
        }
        // Swap the number at endPtr with the empty space until all spaces are filled in
        nums[spaces.poll()] = nums[endPtr]
        nums[endPtr--] = 0
    }

    // At this point we're almost done. However, there are going to be some numbers
    // which were originally moved to the left, which now have spaces
    // between the end of our contiguous numbers and themselves.

    // Increment endptr by one so we advance to the next empty space, copy it to another variable
    // and then set endptr to be at the end of the list of numbers.
    ++endPtr
    var earliestEmpty = endPtr
    endPtr = nums.size - 1

    while (earliestEmpty < endPtr) {
        // If we find a number starting from the end that can be moved forward, we do
        // so.
        if (nums[endPtr] != 0) {
            nums[earliestEmpty++] = nums[endPtr]
            // Earliest empty now points to the next empty spot
        }
        // Keep going back until we hit the truly earliest empty spot
        endPtr--
    }

    for (i in 0..endPtr) {
        total += nums[i] * i
    }

    return total
}
