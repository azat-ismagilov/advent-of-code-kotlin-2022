fun main() {
    val day = 22

    fun <T> List<T>.resizeUp(neededSize: Int, defaultValue: T): List<T> =
        if (neededSize > size) this + List(neededSize - size) { defaultValue } else this

    fun List<Pair<Int, Int>>.append(third: Int) = this.map { (first, second) -> Triple(first, second, third) }

    val bigCube = listOf(
        listOf(0, 1, 2),
        listOf(0, 3, 0),
        listOf(4, 5, 0),
        listOf(6, 0, 0)
    )

    val bigCubeDirections = listOf(
        (1 to 2) to (4 to 0),
        (1 to 3) to (6 to 0),
        (2 to 0) to (5 to 2),
        (2 to 1) to (3 to 2),
        (2 to 3) to (6 to 3),
        (3 to 0) to (2 to 3),
        (3 to 2) to (4 to 1),
        (4 to 2) to (1 to 0),
        (4 to 3) to (3 to 0),
        (5 to 0) to (2 to 2),
        (5 to 1) to (6 to 2),
        (6 to 0) to (5 to 3),
        (6 to 1) to (2 to 1),
        (6 to 2) to (1 to 1)
    )

    val bigCubeSize = 50

    fun buildBigCubeMap(): Map<Triple<Int, Int, Int>, Triple<Int, Int, Int>> {
        val inputMapping = mutableMapOf<Pair<Int, Int>, List<Pair<Int, Int>>>()
        val outputMapping = mutableMapOf<Pair<Int, Int>, List<Pair<Int, Int>>>()
        for (x in bigCube.indices)
            for (y in bigCube[x].indices) {
                val name = bigCube[x][y]
                if (name == 0) continue
                val startX = x * bigCubeSize
                val finishX = x * bigCubeSize + bigCubeSize - 1
                val startY = y * bigCubeSize
                val finishY = y * bigCubeSize + bigCubeSize - 1

                inputMapping[name to 0] = (startX..finishX).map { it to startY }
                inputMapping[name to 1] = (finishY downTo startY).map { startX to it }
                inputMapping[name to 2] = (finishX downTo startX).map { it to finishY }
                inputMapping[name to 3] = (startY..finishY).map { finishX to it }

                outputMapping[name to 0] = (startX..finishX).map { it to finishY }
                outputMapping[name to 1] = (finishY downTo startY).map { finishX to it }
                outputMapping[name to 2] = (finishX downTo startX).map { it to startY }
                outputMapping[name to 3] = (startY..finishY).map { startX to it }
            }
        return bigCubeDirections.flatMap { (from, to) ->
            outputMapping[from]!!.append(from.second).zip(inputMapping[to]!!.append(to.second))
        }.associate { it }
    }

    data class PositionWithDirection(
        val gameField: List<List<Int>>,
        val movesMap: Map<Triple<Int, Int, Int>, Triple<Int, Int, Int>> = mapOf(),
        val x: Int = 0,
        val y: Int = gameField[0].indexOf(0),
        val direction: Int = 0,
    ) {
        private val n: Int
            get() = gameField.size

        private val m: Int
            get() = gameField[x].size

        fun toValue() = 1000 * (x + 1) + 4 * (y + 1) + direction

        fun makeSomething(c: String) = when (c) {
            "L" -> copy(direction = (direction - 1).mod(4))
            "R" -> copy(direction = (direction + 1).mod(4))
            else -> moveSafe(c.toInt())
        }

        fun move() = if (Triple(x, y, direction) in movesMap) {
            val (newX, newY, newDirection) = movesMap[Triple(x, y, direction)]!!
            copy(x = newX, y = newY, direction = newDirection)
        } else when (direction) {
            0 -> copy(y = (y + 1).mod(m))
            1 -> copy(x = (x + 1).mod(n))
            2 -> copy(y = (y - 1).mod(m))
            3 -> copy(x = (x - 1).mod(n))
            else -> this
        }

        fun moveSafe(steps: Int): PositionWithDirection {
            var result = this
            repeat(steps) {
                val prevResult = result
                result = result.move()
                while (gameField[result.x][result.y] == 2)
                    result = result.move()
                if (gameField[result.x][result.y] == 1)
                    return prevResult
            }
            return result
        }
    }

    fun prepareInput(input: List<String>) = input.indexOf("").let { splitIndex ->
        Pair(
            input.take(splitIndex)
                .map { line ->
                    line.map {
                        when (it) {
                            '.' -> 0
                            '#' -> 1
                            else -> 2
                        }
                    }
                }
                .let { field ->
                    val maxLen = field.maxOf { it.size }
                    field.map { it.resizeUp(maxLen, 2) }
                },
            input[splitIndex + 1]
                .replace("R", " R ")
                .replace("L", " L ")
                .split(' ')
        )
    }

    fun part1(input: List<String>): Int {
        val (field, moves) = prepareInput(input)

        var currentPosition = PositionWithDirection(field)

        for (something in moves) {
            currentPosition = currentPosition.makeSomething(something)
        }

        return currentPosition.toValue()
    }

    fun part2(input: List<String>): Int {
        val (field, moves) = prepareInput(input)

        var currentPosition = PositionWithDirection(field, buildBigCubeMap())

        for (something in moves) {
            currentPosition = currentPosition.makeSomething(something)
        }

        return currentPosition.toValue()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 6032)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
