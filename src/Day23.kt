fun main() {
    val day = 23

    data class Position(val x: Int, val y: Int) {
        fun move(direction: Int) = when (direction.mod(4)) {
            0 -> copy(x = x - 1)
            1 -> copy(x = x + 1)
            2 -> copy(y = y - 1)
            else -> copy(y = y + 1)
        }

        fun needToCheck(direction: Int) = when (direction.mod(4)) {
            0, 1 -> move(direction).let { listOf(it, it.move(2), it.move(3)) }
            else -> move(direction).let { listOf(it, it.move(0), it.move(1)) }
        }

        fun hasAnyNeighbours(takenPosition: Set<Position>): Boolean {
            for (newX in x - 1..x + 1)
                for (newY in y - 1..y + 1)
                    if (copy(x = newX, y = newY).let { it != this && it in takenPosition })
                        return true
            return false
        }

        fun tryMove(takenPosition: Set<Position>, startDirection: Int): Position {
            if (!hasAnyNeighbours(takenPosition))
                return this
            for (direction in startDirection..startDirection + 3)
                if (needToCheck(direction).all { it !in takenPosition })
                    return move(direction)
            return this
        }
    }

    fun prepareInput(input: List<String>): Set<Position> {
        val takenPositions = mutableSetOf<Position>()
        for (x in input.indices)
            for (y in input[x].indices)
                if (input[x][y] == '#')
                    takenPositions.add(Position(x, y))
        return takenPositions
    }


    fun <T> Iterable<T>.distance(cmp: (T) -> Int) = maxOf(cmp) - minOf(cmp) + 1

    fun calculateAnswer(takenPositions: Set<Position>) =
        takenPositions.distance { it.x } * takenPositions.distance { it.y } - takenPositions.size

    fun part1(input: List<String>): Int {
        var takenPositions = prepareInput(input)

        repeat(10) { startDirection ->
            val futurePositions = takenPositions.associateWith { it.tryMove(takenPositions, startDirection) }
            val uniquePositions = futurePositions.values.groupBy { it }.filterValues { it.size == 1 }.keys

            takenPositions = futurePositions.map { (from, to) -> if (to in uniquePositions) to else from }.toSet()
        }
        return calculateAnswer(takenPositions)
    }

    fun part2(input: List<String>): Int {
        var takenPositions = prepareInput(input)

        var startDirection = 0
        while (true) {
            val futurePositions = takenPositions.associateWith { it.tryMove(takenPositions, startDirection) }
            val uniquePositions = futurePositions.values.groupBy { it }.filterValues { it.size == 1 }.keys

            val newTakenPositions =
                futurePositions.map { (from, to) -> if (to in uniquePositions) to else from }.toSet()
            if (newTakenPositions == takenPositions)
                break
            takenPositions = newTakenPositions
            startDirection++
        }
        return startDirection + 1
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}


