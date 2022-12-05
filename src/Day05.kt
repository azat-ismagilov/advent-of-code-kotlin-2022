fun main() {
    class Crates(input: List<String>) {
        private val data: MutableList<MutableList<Char>>

        init {
            val columns = input.last().length / 4 + 1
            data = MutableList(columns) { mutableListOf() }
            for (line in input.dropLast(1).reversed()) {
                var column = 0
                for (index in 1 until line.length step 4) {
                    if (line[index] != ' ')
                        data[column].add(line[index])
                    column += 1
                }
            }
        }

        fun move(count: Int, from: Int, to: Int) {
            repeat(count) {
                val tmp = data[from].removeLast()
                data[to].add(tmp)
            }
        }

        fun moveSameOrder(count: Int, from: Int, to: Int) {
            val tmpList = data[from].takeLast(count)
            data[from] = data[from].dropLast(count).toMutableList()
            data[to] = (data[to] + tmpList).toMutableList()
        }

        fun getTopElements(): String = data.joinToString(separator = "") { it.last().toString() }
    }

    fun part1(input: List<String>): String {
        val splitPosition = input.indexOf("")
        val crates = Crates(input.take(splitPosition))
        val moves = input.drop(splitPosition + 1)
        for (line in moves) {
            val regex = """move ([0-9]+) from ([0-9]+) to ([0-9]+)""".toRegex()
            val matchResult = regex.find(line)
            val (count, from, to) = matchResult!!.destructured
            crates.move(count.toInt(), from.toInt() - 1, to.toInt() - 1)
        }
        return crates.getTopElements()
    }


    fun part2(input: List<String>): String {
        val splitPosition = input.indexOf("")
        val crates = Crates(input.take(splitPosition))
        val moves = input.drop(splitPosition + 1)
        for (line in moves) {
            val regex = """move ([0-9]+) from ([0-9]+) to ([0-9]+)""".toRegex()
            val matchResult = regex.find(line)
            val (count, from, to) = matchResult!!.destructured
            crates.moveSameOrder(count.toInt(), from.toInt() - 1, to.toInt() - 1)
        }
        return crates.getTopElements()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
