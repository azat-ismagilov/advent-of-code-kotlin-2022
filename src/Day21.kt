enum class Operation {
    PLUS, MINUS, MUL, DIV
}

data class MonkeyYellOperation(val nameLeft: String, val nameRight: String, val operation: Operation) {
    fun apply(valueLeft: Long, valueRight: Long) = when (operation) {
        Operation.PLUS -> valueLeft + valueRight
        Operation.MINUS -> valueLeft - valueRight
        Operation.MUL -> valueLeft * valueRight
        Operation.DIV -> valueLeft / valueRight
    }

    fun dependentOn() = listOf(nameLeft, nameRight)

    fun getLeftValue(value: Long, wantedResult: Long): Pair<String, Long> = Pair(
        nameLeft, when (operation) {
            Operation.PLUS -> wantedResult - value
            Operation.MINUS -> wantedResult + value
            Operation.MUL -> wantedResult / value
            Operation.DIV -> wantedResult * value
        }
    )

    fun getRightValue(value: Long, wantedResult: Long): Pair<String, Long> = Pair(
        nameRight, when (operation) {
            Operation.PLUS -> wantedResult - value
            Operation.MINUS -> value - wantedResult
            Operation.MUL -> wantedResult / value
            Operation.DIV -> value / wantedResult
        }
    )

    companion object {
        fun of(string: String): MonkeyYellOperation {
            val tokens = string.split(' ')
            val operation = when (tokens[1]) {
                "+" -> Operation.PLUS
                "-" -> Operation.MINUS
                "*" -> Operation.MUL
                "/" -> Operation.DIV
                else -> throw RuntimeException()
            }

            return MonkeyYellOperation(tokens[0], tokens[2], operation)
        }
    }
}

fun String.isLong() = this.toLongOrNull() != null

sealed interface YellingMonkey {
    companion object {
        fun of(string: String): Pair<String, YellingMonkey> {
            val (name, other) = string.split(": ")
            return if (other.isLong()) name to SimpleYellingMonkey(other.toLong())
            else name to ComplexYellingMonkey(MonkeyYellOperation.of(other))
        }
    }
}


data class SimpleYellingMonkey(val value: Long) : YellingMonkey

data class ComplexYellingMonkey(val yellOperation: MonkeyYellOperation) : YellingMonkey

fun main() {
    val day = 21

    fun part1(input: List<String>): Long {
        val monkeys = input.associate { YellingMonkey.of(it) }.toMutableMap()

        repeat(monkeys.size) {
            for ((name, currentMonkey) in monkeys) {
                if (currentMonkey is ComplexYellingMonkey) {
                    with(currentMonkey.yellOperation) {
                        val dependentMonkeys = dependentOn().map { searchName ->
                            monkeys[searchName]
                        }
                        if (dependentMonkeys.all { it is SimpleYellingMonkey }) {
                            val (leftValue, rightValue) = dependentMonkeys.map { (it as SimpleYellingMonkey).value }
                            monkeys[name] = SimpleYellingMonkey(apply(leftValue, rightValue))
                        }
                    }
                }

                if (name == "root" && currentMonkey is SimpleYellingMonkey) {
                    return currentMonkey.value
                }
            }
        }
        return 0
    }


    fun part2(input: List<String>): Long {
        val monkeys = input.associate { YellingMonkey.of(it) }.toMutableMap()
        monkeys.remove("humn")
        val root = monkeys["root"] as ComplexYellingMonkey
        monkeys.remove("root")

        var someThingInterestingHappened = true
        while (someThingInterestingHappened) {
            someThingInterestingHappened = false
            for ((name, currentMonkey) in monkeys) {
                if (currentMonkey is ComplexYellingMonkey) {
                    with(currentMonkey.yellOperation) {
                        val dependentMonkeys = dependentOn().map { searchName ->
                            monkeys[searchName]
                        }
                        if (dependentMonkeys.all { it is SimpleYellingMonkey }) {
                            val (leftValue, rightValue) = dependentMonkeys.map { (it as SimpleYellingMonkey).value }
                            monkeys[name] = SimpleYellingMonkey(apply(leftValue, rightValue))
                            someThingInterestingHappened = true
                        }
                    }
                }
            }
        }
        var necessaryPair = with(root.yellOperation) {
            val (leftMonkey, rightMonkey) = dependentOn().map { searchName ->
                searchName to monkeys[searchName]
            }
            if (leftMonkey.second is SimpleYellingMonkey)
                rightMonkey.first to (leftMonkey.second as SimpleYellingMonkey).value
            else
                leftMonkey.first to (rightMonkey.second as SimpleYellingMonkey).value
        }

        while (necessaryPair.first != "humn")
            with((monkeys[necessaryPair.first] as ComplexYellingMonkey).yellOperation) {
                val (leftMonkey, rightMonkey) = dependentOn().map { searchName ->
                    searchName to monkeys[searchName]
                }
                necessaryPair = if (leftMonkey.second is SimpleYellingMonkey)
                    getRightValue((leftMonkey.second as SimpleYellingMonkey).value, necessaryPair.second)
                else
                    getLeftValue((rightMonkey.second as SimpleYellingMonkey).value, necessaryPair.second)
            }
        return necessaryPair.second
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))

}

