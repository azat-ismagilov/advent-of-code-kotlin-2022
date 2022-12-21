fun main() {
    val day = 20

    data class IndexedNumber(val index: Int, val number: Long)


    fun part1(input: List<String>): Long {
        val array = input.mapIndexed { index, c -> IndexedNumber(index, c.toLong()) }

        val interestingPoints = listOf(1000, 2000, 3000)

        val mutableArray = array.toMutableList()

        for (element in array) {
            val index = mutableArray.indexOf(element)
            mutableArray.removeAt(index)
            val newIndex = (index + element.number).mod(mutableArray.size)
            mutableArray.add(newIndex, element)
        }

        return interestingPoints.sumOf { delay ->
            mutableArray[(mutableArray.indexOfFirst { it.number == 0L } + delay).mod(mutableArray.size)].number
        }
    }

    fun part2(input: List<String>): Long {
        val array = input.mapIndexed { index, c -> IndexedNumber(index, 811589153L * c.toLong()) }

        val interestingPoints = listOf(1000, 2000, 3000)

        val mutableArray = array.toMutableList()

        repeat(10) {
            for (element in array) {
                val index = mutableArray.indexOf(element)
                mutableArray.removeAt(index)
                val newIndex = (index + element.number).mod(mutableArray.size)
                mutableArray.add(newIndex, element)
            }
        }

        return interestingPoints.sumOf { delay ->
            mutableArray[(mutableArray.indexOfFirst { it.number == 0L } + delay).mod(mutableArray.size)].number
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))

}

