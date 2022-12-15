sealed interface DistressSignal : Comparable<DistressSignal> {
    override operator fun compareTo(other: DistressSignal): Int

    companion object {
        fun of(string: String): DistressSignal =
            if (string.startsWith('[')) DistressList.of(string)
            else DistressInt.of(string)
    }

}

data class DistressList(val content: List<DistressSignal>) : DistressSignal {

    override operator fun compareTo(other: DistressSignal): Int = when (other) {
        is DistressInt -> this.compareTo(DistressList(listOf(other)))
        is DistressList -> this.content.compareTo(other.content)
    }

    companion object {
        fun of(string: String): DistressList {
            val lst = string.removePrefix("[").removeSuffix("]").splitSquares()
            return DistressList(lst.map { DistressSignal.of(it) })
        }
    }
}

private fun String.splitSquares(): List<String> {
    val result = mutableListOf<String>()
    var currentString = ""
    var balance = 0

    for (c in this) {
        balance += when (c) {
            '[' -> 1
            ']' -> -1
            else -> 0
        }
        if (c == ',' && balance == 0) {
            result.add(currentString)
            currentString = ""
        } else currentString += c
    }
    if (currentString != "") result.add(currentString)
    return result
}

private fun List<DistressSignal>.compareTo(other: List<DistressSignal>): Int {
    for ((x, y) in this.zip(other)) {
        when (x.compareTo(y)) {
            -1 -> return -1
            1 -> return 1
        }
    }
    return this.size.compareTo(other.size)
}

data class DistressInt(val content: Int) : DistressSignal {

    override operator fun compareTo(other: DistressSignal): Int = when (other) {
        is DistressInt -> this.content.compareTo(other.content)
        is DistressList -> DistressList(listOf(this)).compareTo(other)
    }

    companion object {
        fun of(string: String): DistressInt = DistressInt(string.toInt())
    }
}


fun main() {
    val day = 13

    fun part1(input: List<String>): Int = input.chunked(3).mapIndexed { index, (first, second, _) ->
        if (DistressSignal.of(first) < DistressSignal.of(second)) index + 1
        else 0
    }.sum()

    val dividers = listOf(
        DistressSignal.of("[[2]]"), DistressSignal.of("[[6]]")
    )

    fun part2(input: List<String>): Int =
        (dividers + input.filterIndexed { index, _ -> index % 3 < 2 }.map { DistressSignal.of(it) })
            .sorted()
            .let {
                (it.indexOf(dividers[0]) + 1) * (it.indexOf(dividers[1]) + 1)
            }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
