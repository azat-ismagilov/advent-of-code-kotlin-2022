import java.lang.RuntimeException
import kotlin.math.*

object Day09 {
    data class Position(val x: Int = 0, val y: Int = 0) {
        fun distance(other: Position) = max(abs(x - other.x), abs(y - other.y))
        fun differenceVector(other: Position) = Position((other.x - x).sign, (other.y - y).sign)
        operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    }

    sealed interface Move {
        val steps: Int
    }

    class MoveUp(override val steps: Int) : Move
    class MoveDown(override val steps: Int) : Move
    class MoveLeft(override val steps: Int) : Move
    class MoveRight(override val steps: Int) : Move

    class Rope(countTails: Int = 2) {
        private var positions = MutableList(countTails) { Position() }

        private val visitedTailInternal = mutableListOf<Position>()

        val visitedTail get() = visitedTailInternal.toList()

        private fun adjustTails() {
            for (index in 1 until positions.size)
                if (positions[index].distance(positions[index - 1]) > 1)
                    positions[index] += positions[index].differenceVector(positions[index - 1])

            visitedTailInternal.add(positions.last())
        }

        private fun processMove(move: Move): Rope {
            repeat(move.steps) {
                positions[0] = when (move) {
                    is MoveUp -> positions[0].copy(y = positions[0].y - 1)
                    is MoveDown -> positions[0].copy(y = positions[0].y + 1)
                    is MoveLeft -> positions[0].copy(x = positions[0].x - 1)
                    is MoveRight -> positions[0].copy(x = positions[0].x + 1)
                }
                adjustTails()
            }
            return this
        }

        fun processMoves(moves: Iterable<Move>): Rope {
            moves.forEach { processMove(it) }
            return this
        }
    }

    fun part1(input: List<String>): Int = Rope()
        .processMoves(input.toMoves()).visitedTail.toSet().size

    fun part2(input: List<String>): Int = Rope(10)
        .processMoves(input.toMoves()).visitedTail.toSet().size

    private fun List<String>.toMoves() = this.map {
        val split = it.split(' ')
        val steps = split[1].toInt()
        when (split[0]) {
            "U" -> MoveUp(steps)
            "D" -> MoveDown(steps)
            "L" -> MoveLeft(steps)
            "R" -> MoveRight(steps)
            else -> throw RuntimeException()
        }
    }
}

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(Day09.part1(testInput) == 13)
    val testInput2 = readInput("Day09_test2")
    check(Day09.part2(testInput2) == 36)

    val input = readInput("Day09")
    println(Day09.part1(input))
    println(Day09.part2(input))
}
