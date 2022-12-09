fun main() {

    fun List<String>.parseTrees(): List<List<Int>> = this.map { line -> line.map { it.digitToInt() } }

    fun getPossibleDirections(n: Int, m: Int): List<List<List<Pair<Int, Int>>>> {
        val xCoordinates = (0 until n)
        val yCoordinates = (0 until m)
        return listOf(
            xCoordinates.map { x -> yCoordinates.map { y -> Pair(x, y) } },
            xCoordinates.map { x -> yCoordinates.reversed().map { y -> Pair(x, y) } },
            yCoordinates.map { y -> xCoordinates.map { x -> Pair(x, y) } },
            yCoordinates.map { y -> xCoordinates.reversed().map { x -> Pair(x, y) } }
        )
    }

    fun List<List<Int>>.isVisible(): List<List<Boolean>> {
        val n = this.size
        val m = this.first().size
        val result = List(n) { MutableList(m) { false } }
        val possibleDirections = getPossibleDirections(n, m)
        for (indexes in possibleDirections)
            for (indexSequence in indexes) {
                var maxHeight = -1
                indexSequence.forEach { (x, y) ->
                    if (maxHeight < this[x][y]) {
                        result[x][y] = true
                        maxHeight = this[x][y]
                    }
                }
            }
        return result
    }

    data class TreeInLine(val position: Int, val height: Int)

    fun List<List<Int>>.treeHouseVisible(): List<List<Int>> {
        val n = this.size
        val m = this.first().size
        val result = List(n) { MutableList(m) { 1 } }
        val possibleDirections = getPossibleDirections(n, m)
        for (indexes in possibleDirections)
            for (indexSequence in indexes) {
                val queueVisible = mutableListOf(TreeInLine(0, Int.MAX_VALUE))
                indexSequence.forEachIndexed { index, (x, y) ->
                    val currentTreeHeight = this[x][y]
                    while (queueVisible.last().height < currentTreeHeight)
                        queueVisible.removeLast()
                    result[x][y] *= index - queueVisible.last().position
                    queueVisible.add(TreeInLine(index, currentTreeHeight))
                }
            }
        return result
    }

    fun part1(input: List<String>): Int = input.parseTrees().isVisible().sumOf { it.count { it } }

    fun part2(input: List<String>): Int = input.parseTrees().treeHouseVisible().maxOf { it.maxOf { it } }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
