import kotlin.math.abs

fun main() {
    val day = 12

    fun Char.toHeight() = when (this) {
        in 'a'..'z' -> this - 'a'
        'S' -> 0
        'E' -> 'z' - 'a'
        else -> throw RuntimeException()
    }

    fun part1(input: List<String>): Int {
        val n = input.size
        val m = input.first().length
        val closestDistance = List(n) { MutableList(m) { Int.MAX_VALUE - 1 } }
        for (i in 0 until n)
            for (j in 0 until m)
                if (input[i][j] == 'E')
                    closestDistance[i][j] = 0
        repeat(n * m) {
            for (i in 0 until n)
                for (j in 0 until m)
                    for (x in maxOf(0, i - 1) until minOf(i + 2, n))
                        for (y in maxOf(0, j - 1) until minOf(j + 2, m))
                            if (abs(x - i) + abs(y - j) == 1)
                                if (input[i][j].toHeight() + 1 >= input[x][y].toHeight())
                                    closestDistance[i][j] = minOf(closestDistance[i][j], closestDistance[x][y] + 1)

        }
        for (i in 0 until n)
            for (j in 0 until m)
                if (input[i][j] == 'S')
                    return closestDistance[i][j]
        throw RuntimeException("No start position")
    }

    fun part2(input: List<String>): Int {
        val n = input.size
        val m = input.first().length
        val closestDistance = List(n) { MutableList(m) { Int.MAX_VALUE - 1 } }
        for (i in 0 until n)
            for (j in 0 until m)
                if (input[i][j] == 'E')
                    closestDistance[i][j] = 0
        repeat(n * m) {
            for (i in 0 until n)
                for (j in 0 until m)
                    for (x in maxOf(0, i - 1) until minOf(i + 2, n))
                        for (y in maxOf(0, j - 1) until minOf(j + 2, m))
                            if (abs(x - i) + abs(y - j) == 1)
                                if (input[i][j].toHeight() + 1 >= input[x][y].toHeight())
                                    closestDistance[i][j] = minOf(closestDistance[i][j], closestDistance[x][y] + 1)

        }
        var result = Int.MAX_VALUE
        for (i in 0 until n)
            for (j in 0 until m)
                if (input[i][j].toHeight() == 0)
                    result = minOf(result, closestDistance[i][j])
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
