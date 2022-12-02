fun main() {
    fun splitInput(input: List<String>): List<List<String>> {
        val result = mutableListOf<List<String>>()
        var current = mutableListOf<String>()
        for (line in input) {
            if (line == "") {
                result.add(current)
                current = mutableListOf()
            } else
                current.add(line)
        }
        if (current.isNotEmpty())
            result.add(current)
        return result
    }

    fun part1(input: List<String>): Int = splitInput(input).maxOf { it.sumOf { it.toInt() } }

    fun part2(input: List<String>): Int =
        splitInput(input).map { it.sumOf { it.toInt() } }.sortedDescending().take(3).sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
