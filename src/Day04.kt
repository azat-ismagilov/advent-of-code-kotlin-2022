import kotlin.math.max
import kotlin.math.min

fun main() {
    fun IntRange.contains(other: IntRange) = other.first in this && other.last in this
    fun IntRange.overlaps(other: IntRange) = max(first, other.first) <= min(last, other.last)

    fun <T> List<T>.toPair(): Pair<T, T> =
        if (this.size != 2) throw IllegalArgumentException("List is not of length 2!")
        else Pair(this[0], this[1])

    fun String.toPairOfRanges(): Pair<IntRange, IntRange> = this.split(',', '-')
        .map { it.toInt() }
        .chunked(2)
        .map { IntRange(it[0], it[1]) }
        .toPair()

    fun part1(input: List<String>): Int =
        input.count { line ->
            line.toPairOfRanges().let { (a, b) -> a.contains(b) || b.contains(a) }
        }


    fun part2(input: List<String>): Int =
        input.count { line ->
            line.toPairOfRanges().let { (a, b) -> a.overlaps(b) }
        }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
