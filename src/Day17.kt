private val List<List<Boolean>>.height: Int
    get() = this.indexOfLast { line -> line.any { it } }

data class Block(val data: List<List<Boolean>>) {
    fun intersects(other: List<List<Boolean>>): Boolean = data.zip(other).any { (firstLine, secondLine) ->
        firstLine.zip(secondLine).any { (bitFirst, bitSecond) ->
            bitFirst and bitSecond
        }
    }

    val hSize: Int
        get() = data.size

    val vSize: Int
        get() = data.first().size

    companion object {
        fun of(input: String) = Block(input.split('\n').map { line -> line.map { it == '#' } })
    }
}

data class MovableBlock(val block: Block, val positionX: Int, val positionY: Int) {
    fun move(direction: BlockDirection, shaft: List<List<Boolean>>): MovableBlock {
        val newBlock = this.copy(
            positionX = positionX + when (direction) {
                BlockDirection.LEFT -> -1
                BlockDirection.RIGHT -> 1
            }
        )
        if (!newBlock.intersects(shaft))
            return newBlock
        return this
    }

    fun moveDown(shaft: List<List<Boolean>>): MovableBlock? {
        val newBlock = this.copy(
            positionY = positionY - 1
        )
        if (!newBlock.intersects(shaft))
            return newBlock
        return null
    }

    fun intersects(shaft: List<List<Boolean>>): Boolean {
        if (positionX < 0 || positionX + block.vSize > shaft.first().size) {
            return true
        }
        if (positionY < 0 || positionY + block.hSize > shaft.size) {
            return true
        }
        val cutOut =
            shaft.subList(positionY, positionY + block.hSize).map { it.subList(positionX, positionX + block.vSize) }

        return block.intersects(cutOut)
    }

    fun applyToShaft(shaft: MutableList<MutableList<Boolean>>) {
        for (y in 0 until block.hSize)
            for (x in 0 until block.vSize)
                if (block.data[y][x])
                    shaft[y + positionY][x + positionX] = true
    }
}

enum class BlockDirection {
    LEFT, RIGHT
}

fun main() {
    val day = 17

    val shapes = listOf(
        """
            ####
        """.trimIndent(),
        """
            .#.
            ###
            .#.
        """.trimIndent(),
        """
            ###
            ..#
            ..#
        """.trimIndent(),
        """
            #
            #
            #
            #
        """.trimIndent(),
        """
            ##
            ##
        """.trimIndent()
    ).map { Block.of(it) }


    fun prepareInput(input: List<String>): List<BlockDirection> = input.first().map {
        when (it) {
            '<' -> BlockDirection.LEFT
            else -> BlockDirection.RIGHT
        }
    }

    fun <T> List<T>.getCyclic(index: Int) = this[index % size]

    fun part1(input: List<String>, shaftSize: Int, rocksCount: Int): Int {
        val moves = prepareInput(input)
        val shaft = mutableListOf<MutableList<Boolean>>()
        var currentMove = 0
        var requiredRocks = rocksCount
        var rockIndex = 0
        while (requiredRocks > 0) {
            val shape = shapes.getCyclic(rockIndex++)
            val neededDefaultPosition = shaft.height + 4
            while (shaft.size < neededDefaultPosition + shape.hSize)
                shaft.add(MutableList(shaftSize) { false })

            var movableBlock = MovableBlock(shape, 2, neededDefaultPosition)
            while (true) {
                movableBlock = movableBlock.move(moves.getCyclic(currentMove++), shaft)
                movableBlock = movableBlock.moveDown(shaft) ?: break
            }
            movableBlock.applyToShaft(shaft)
            --requiredRocks
        }
        return shaft.height + 1
    }

    fun part2(input: List<String>, shaftSize: Int, rocksCount: Long): Long {
        val moves = prepareInput(input)
        val shaft = mutableListOf<MutableList<Boolean>>()
        var currentMove = 0
        val prevAttempts = mutableMapOf<Triple<List<List<Boolean>>, Int, Int>, Pair<Int, Int>>()
        var requiredRocks = rocksCount
        var fakeHeight = 0L
        var rockIndex = 0
        while (requiredRocks > 0) {
            val shape = shapes.getCyclic(rockIndex++)
            val neededDefaultPosition = shaft.height + 4
            while (shaft.size < neededDefaultPosition + shape.hSize)
                shaft.add(MutableList(shaftSize) { false })

            var movableBlock = MovableBlock(shape, 2, neededDefaultPosition)
            while (true) {
                movableBlock = movableBlock.move(moves.getCyclic(currentMove++), shaft)
                movableBlock = movableBlock.moveDown(shaft) ?: break
            }
            movableBlock.applyToShaft(shaft)
            val myCurrentResult = Triple(
                shaft.take(shaft.height).takeLast(5), rockIndex % shapes.size, currentMove % moves.size
            )
            --requiredRocks
            if (myCurrentResult in prevAttempts) {
                val (prevRockIndex, prevHeight) = prevAttempts[myCurrentResult]!!
                val d = requiredRocks / (rockIndex - prevRockIndex)
                requiredRocks -= d * (rockIndex - prevRockIndex)
                fakeHeight += d * (shaft.height - prevHeight)

                prevAttempts.clear()
            }
            prevAttempts[myCurrentResult] = Pair(rockIndex, shaft.height)
        }
        return shaft.height + fakeHeight + 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput, 7, 2022) == 3068)
    check(part2(testInput, 7, 1000000000000) == 1514285714288)

    val input = readInput("Day${day}")
    println(part1(input, 7, 2022))
    println(part2(input, 7, 1000000000000))
}
