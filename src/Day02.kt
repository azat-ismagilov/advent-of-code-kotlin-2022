fun main() {
    fun String.toNumber(): Int = when (this) {
        "X" -> 1
        "Y" -> 2
        "Z" -> 3
        "A" -> 1
        "B" -> 2
        "C" -> 3
        else -> 0
    }

    fun win(players: List<Int>): Int = ((players[1] - players[0] + 4) % 3) * 3

    fun calcMyPrice(players: List<Int>): Int = win(players) + players[1]

    fun part1(input: List<String>): Int = input.sumOf { calcMyPrice(it.split(" ").map { it.toNumber() }) }

    fun calcWinningsWithHelp(players: List<Int>): Int = (players[0] + players[1]) % 3 + 1 + (players[1] - 1) * 3

    fun part2(input: List<String>): Int = input.sumOf { calcWinningsWithHelp(it.split(" ").map { it.toNumber() }) }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
