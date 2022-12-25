fun main() {
    val day = 24

    fun Char.toDirection() = when (this) {
        '>' -> 0
        'v' -> 1
        '<' -> 2
        else -> 3
    }

    data class Position(val x: Int, val y: Int) {
        fun move(steps: Int, direction: Int) = when (direction) {
            0 -> copy(y = y + steps)
            1 -> copy(x = x + steps)
            2 -> copy(y = y - steps)
            3 -> copy(x = x - steps)
            else -> this
        }
    }

    data class WindPiece(val position: Position, val direction: Int) {
        fun move(steps: Int) = copy(position = position.move(steps, direction))

        fun putInside(n: Int, m: Int) = this.copy(position = Position(position.x.mod(n), position.y.mod(m)))

        constructor(x: Int, y: Int, charDirection: Char) : this(Position(x, y), charDirection.toDirection())
    }

    data class WindGame(val n: Int, val m: Int, val pieces: List<WindPiece>)

    fun List<String>.removeWrapper() = this.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }

    fun windPiecesFromLines(lines: List<String>) = lines.flatMapIndexed { x, line ->
        line.mapIndexed { y, c ->
            if (c == '.') null
            else WindPiece(x, y, c)
        }.filterNotNull()
    }

    fun List<String>.toWindGame() = removeWrapper().let { lines ->
        WindGame(lines.size, lines.first().length, windPiecesFromLines(lines))
    }

    fun isInside(n: Int, m: Int, position: Position) =
        (position.x in 0 until n && position.y in 0 until m)

    fun stepsToMove(game: WindGame, startPosition: Position, finishPosition: Position, initialSteps: Int = 0): Int {
        var possiblePositions = listOf(startPosition)
        var steps = initialSteps
        while (finishPosition !in possiblePositions) {
            steps++

            val possibleMoves =
                (-1..3).flatMap { direction ->
                    possiblePositions.map { it.move(1, direction) }
                }.toSet()
            val windPositions =
                game.pieces.map {
                    it.move(steps).putInside(game.n, game.m).position
                }.toSet()
            possiblePositions = possibleMoves.subtract(windPositions)
                .filter { isInside(game.n, game.m, it) || it in listOf(startPosition, finishPosition) }
        }
        return steps
    }

    fun part1(input: List<String>): Int {
        val game = input.toWindGame()
        return stepsToMove(game, Position(-1, 0), Position(game.n, game.m - 1))
    }

    fun part2(input: List<String>): Int {
        val game = input.toWindGame()
        val startPosition = Position(-1, 0)
        val finishPosition = Position(game.n, game.m - 1)
        var steps = stepsToMove(game, startPosition, finishPosition)
        steps = stepsToMove(game, finishPosition, startPosition, steps)
        steps = stepsToMove(game, startPosition, finishPosition, steps)
        return steps
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}



