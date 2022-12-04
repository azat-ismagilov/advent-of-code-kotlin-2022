fun main() {
    fun Char.toNumber(): Int = when (this) {
        in 'a' .. 'z' -> this - 'a' + 1
        in 'A' .. 'Z' -> this - 'A' + 27
        else -> 0
    }

    fun calcPrice(left: String, right: String) = left.toSet().intersect(right.toSet()).sumOf { it.toNumber() }

    fun part1(input: List<String>): Int = input.sumOf { calcPrice(it.take(it.length / 2), it.drop(it.length / 2)) }

    fun solvePart2(line: List<Set<Char>>): Int = line[0].intersect(line[1]).intersect(line[2]).sumOf { it.toNumber() }

    fun part2(input: List<String>): Int = input.chunked(3).sumOf { solvePart2(it.map { it.toSet() }) }

    
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
