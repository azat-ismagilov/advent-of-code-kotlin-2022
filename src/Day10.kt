import kotlin.math.*

fun main() {
    fun List<String>.toRegisterPoints(): List<Int> {
        var currentValue = 1

        val result = mutableListOf(currentValue)
        for (line in this) {
            val commands = line.split(' ')
            when (commands[0]) {
                "noop" -> result.add(currentValue)
                "addx" -> {
                    result.add(currentValue)
                    currentValue += commands[1].toInt()
                    result.add(currentValue)
                }
            }
        }
        return result
    }

    val interestingCycles = 20..220 step 40

    fun part1(input: List<String>): Int = input.toRegisterPoints().let { registerCycles ->
        interestingCycles.sumOf {
            registerCycles[it - 1] * it
        }
    }

    fun part2(input: List<String>): List<String> = input.toRegisterPoints()
        .chunked(40)
        .map { lineRegister ->
            lineRegister.mapIndexed { index, position ->
                if (abs(index - position) <= 1)
                    '#'
                else
                    '.'
            }.joinToString(separator = "")
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    println(part2(testInput).joinToString(separator = "\n"))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input).joinToString(separator = "\n"))
}
