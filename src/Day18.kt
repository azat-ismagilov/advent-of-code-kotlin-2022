data class Cube3D(val x: Int, val y: Int, val z: Int) {
    private fun toList() = listOf(x, y, z)

    constructor(list: List<Int>) : this(list[0], list[1], list[2])

    private operator fun get(index: Int) = toList()[index]

    fun adjacentBlock(side: Int): Cube3D {
        val coordinates = this.toList().toMutableList()
        when (side) {
            in 0 until 3 -> coordinates[side % 3]++
            else -> coordinates[side % 3]--
        }
        return Cube3D(coordinates)
    }

    fun isAdjacent(other: Cube3D, side: Int): Boolean = other == adjacentBlock(side)

    companion object {
        fun of(string: String): Cube3D {
            val (x, y, z) = string.split(',').map { it.toInt() }
            return Cube3D(x, y, z)
        }
    }
}

fun main() {
    val day = 18

    fun prepareInput(input: List<String>) = input.map { Cube3D.of(it) }

    fun part1(input: List<String>): Int {
        val cubes = prepareInput(input)

        return (0 until 6).sumOf { side ->
            cubes.count { cube ->
                cubes.none { otherCube -> cube.isAdjacent(otherCube, side) }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val cubes = prepareInput(input)

        var possibleSides = cubes.flatMap { cube ->
            (0 until 6).map { side ->
                cube.adjacentBlock(side)
            }
        }

        possibleSides = possibleSides.filter { it !in cubes }

        val canEscape = mutableSetOf<Cube3D>()
        val cannotEscape = mutableSetOf<Cube3D>()

        for (block in possibleSides) {
            val queue = mutableListOf(block)
            val used = mutableSetOf(block)

            var isPossibleToLeave: Boolean? = null

            while (queue.isNotEmpty()) {
                val cube = queue.removeFirst()
                if (cube in canEscape) {
                    isPossibleToLeave = true
                    break
                }
                if (cube in cannotEscape) {
                    isPossibleToLeave = false
                    break
                }
                if (used.size >= cubes.size * cubes.size) {
                    isPossibleToLeave = true
                    break
                }

                for (side in (0 until 6)) {
                    val newBlock = cube.adjacentBlock(side)
                    if (newBlock !in used && newBlock !in cubes) {
                        queue.add(newBlock)
                        used.add(newBlock)
                    }
                }
            }

            if (isPossibleToLeave == true)
                for (cube in used)
                    canEscape.add(cube)
            else
                for (cube in used)
                    cannotEscape.add(cube)
        }

        return possibleSides.count { it in canEscape }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
