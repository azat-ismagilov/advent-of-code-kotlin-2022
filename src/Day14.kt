data class SandPosition(val x: Int, val y: Int) {
    companion object {
        fun of(string: String): SandPosition {
            val (x, y) = string.split(',').map { it.toInt() }
            return SandPosition(x, y)
        }
    }
}


fun main() {
    val day = 14

    val start = SandPosition(500, 0)

    fun unorderedRange(start: Int, finish: Int): IntRange = if (start < finish) {
        start..finish
    } else {
        finish..start
    }

    fun rangeOfSandPositions(position1: SandPosition, position2: SandPosition) = if (position1.x == position2.x) {
        unorderedRange(position1.y, position2.y).map { SandPosition(position1.x, it) }
    } else {
        unorderedRange(position1.x, position2.x).map { SandPosition(it, position1.y) }
    }

    fun prepareInput(input: List<String>): MutableList<SandPosition> {
        val segments = input.map { line -> line.split(" -> ").map { SandPosition.of(it) } }
        val points = segments.flatMap { segment ->
            segment.zipWithNext().flatMap { (position1, position2) ->
                rangeOfSandPositions(position1, position2)
            }
        }.distinct().toMutableList()
        return points
    }

    fun SandPosition.fallDown(points: List<SandPosition>): SandPosition? {
        val newY = points.filter { it.x == this.x && it.y > this.y }
            .minOfOrNull { it.y } ?: return null //infinite fall

        return this.copy(
            y = newY - 1
        )
    }

    fun SandPosition.moveSides(points: List<SandPosition>): SandPosition? {
        for (x in listOf(this.x - 1, this.x + 1)) {
            val nextPossiblePosition = SandPosition(x, this.y + 1)
            if (nextPossiblePosition !in points) {
                return nextPossiblePosition
            }
        }
        return null
    }

    fun part1(input: List<String>): Int {
        val points = prepareInput(input)

        var successfulFallAttempt = 0
        loop@ while (true) {
            var sandBlockPosition = start
            while (true) {
                sandBlockPosition = sandBlockPosition.fallDown(points) ?: break@loop
                sandBlockPosition = sandBlockPosition.moveSides(points) ?: break
            }
            points.add(sandBlockPosition)
            successfulFallAttempt++
        }
        return successfulFallAttempt
    }

    fun part2(input: List<String>): Int {
        val points = prepareInput(input)

        val maxY = points.maxOf { it.y } + 2

        for (x in 500 - maxY - 1..500 + maxY + 1)
            points.add(SandPosition(x, maxY))

        var successfulFallAttempt = 0
        loop@ while (true) {
            var sandBlockPosition = start
            while (true) {
                sandBlockPosition = sandBlockPosition.fallDown(points) ?: break@loop
                sandBlockPosition = sandBlockPosition.moveSides(points) ?: break
            }
            points.add(sandBlockPosition)
            successfulFallAttempt++
            if (sandBlockPosition == start)
                break
        }
        return successfulFallAttempt
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
