import java.lang.RuntimeException

//I was planning to put some nice code here, but it was 2 in the morning
data class OnlyMods(var mods: Map<Int, Int>?, val initialValue: Int? = null) {
    fun updateMods(newMods: Iterable<Int>) {
        mods = newMods.associateWith { mod -> initialValue!! % mod }
    }

    fun fromInt(other: Int) = OnlyMods(mods!!.mapValues { (mod, _) -> other % mod })

    operator fun plus(other: OnlyMods): OnlyMods =
        OnlyMods(mods!!.mapValues { (key, value) -> (value + other.mods!![key]!!) % key })

    operator fun times(other: OnlyMods): OnlyMods =
        OnlyMods(mods!!.mapValues { (key, value) -> (value * other.mods!![key]!!) % key })

    operator fun rem(other: Int) = mods!![other]!!
}

private class MonkeyTest(
    val divisibleBy: Int,
    private val monkeyIdIfTrue: Int,
    private val monkeyIdIfFalse: Int
) {

    fun apply(old: Int) =
        if (old % divisibleBy == 0) monkeyIdIfTrue else monkeyIdIfFalse

    fun apply(old: OnlyMods) =
        if (old % divisibleBy == 0) monkeyIdIfTrue else monkeyIdIfFalse

    companion object {
        fun of(input: List<String>): MonkeyTest {
            val divisibleBy = input[0].removePrefix("  Test: divisible by ").toInt()
            val monkeyIdIfTrue = input[1].removePrefix("    If true: throw to monkey ").toInt()
            val monkeyIdIfFalse = input[2].removePrefix("    If false: throw to monkey ").toInt()

            return MonkeyTest(divisibleBy, monkeyIdIfTrue, monkeyIdIfFalse)
        }
    }
}

private class MonkeyOperation(private val modifyValue: (Int) -> Int) {
    operator fun invoke(old: Int) = modifyValue(old)

    companion object {
        fun of(inputLine: String): MonkeyOperation {
            val stripped = inputLine.removePrefix("  Operation: new = ").split(' ')
            return MonkeyOperation { old ->
                val (left, right) = arrayOf(stripped[0], stripped[2]).map {
                    if (it == "old") old else it.toInt()
                }
                when (stripped[1]) {
                    "+" -> left + right
                    "*" -> left * right
                    else -> throw RuntimeException("Error while parsing")
                }
            }
        }
    }
}

private class AdvancedMonkeyOperation(private val modifyValue: (OnlyMods) -> OnlyMods) {
    operator fun invoke(old: OnlyMods) = modifyValue(old)

    companion object {
        fun of(inputLine: String): AdvancedMonkeyOperation {
            val stripped = inputLine.removePrefix("  Operation: new = ").split(' ')
            return AdvancedMonkeyOperation { old ->
                val (left, right) = arrayOf(stripped[0], stripped[2]).map {
                    if (it == "old") old else old.fromInt(it.toInt())
                }
                when (stripped[1]) {
                    "+" -> left + right
                    "*" -> left * right
                    else -> throw RuntimeException("Error while parsing")
                }
            }
        }
    }
}


private data class Monkey(
    val items: MutableList<Int>,
    val operation: MonkeyOperation,
    val test: MonkeyTest,
    var itemsInspected: Int
) {
    companion object {
        fun of(input: List<String>): Monkey {
            val items = input[1].removePrefix("  Starting items: ")
                .split(", ")
                .map { it.toInt() }
                .toMutableList()
            val operation = MonkeyOperation.of(input[2])
            val test = MonkeyTest.of(input.subList(3, 6))
            return Monkey(items, operation, test, 0)
        }
    }
}

private data class AdvancedMonkey(
    val items: MutableList<OnlyMods>,
    val operation: AdvancedMonkeyOperation,
    val test: MonkeyTest,
    var itemsInspected: Int
) {
    companion object {
        fun of(input: List<String>): AdvancedMonkey {
            val items = input[1].removePrefix("  Starting items: ")
                .split(", ")
                .map { OnlyMods(null, it.toInt()) }
                .toMutableList()
            val operation = AdvancedMonkeyOperation.of(input[2])
            val test = MonkeyTest.of(input.subList(3, 6))
            return AdvancedMonkey(items, operation, test, 0)
        }
    }
}

fun main() {
    val day = 11

    fun part1(input: List<String>) = input.chunked(7)
        .map { Monkey.of(it) }
        .let { monkeys ->
            repeat(20) {
                for (monkey in monkeys) {
                    while (monkey.items.isNotEmpty()) {
                        monkey.itemsInspected++
                        var item = monkey.items.removeFirst()
                        item = monkey.operation(item) / 3
                        val newMonkey = monkey.test.apply(item)
                        monkeys[newMonkey].items.add(item)
                    }
                }
            }
            monkeys
        }.map { it.itemsInspected }
        .sorted().takeLast(2)
        .let { (first, second) -> first * second }

    fun part2(input: List<String>) = input.chunked(7)
        .map { AdvancedMonkey.of(it) }
        .let { monkeys ->
            val mods = monkeys.map { it.test.divisibleBy }.toSet()
            monkeys.forEach { monkey ->
                monkey.items.forEach { item -> item.updateMods(mods) }
            }
            monkeys
        }
        .let { monkeys ->
            repeat(10000) {
                monkeys.forEach { monkey ->
                    while (monkey.items.isNotEmpty()) {
                        monkey.itemsInspected++
                        var item = monkey.items.removeFirst()
                        item = monkey.operation(item)
                        val newMonkey = monkey.test.apply(item)
                        monkeys[newMonkey].items.add(item)
                    }
                }
            }
            monkeys
        }.map { it.itemsInspected.toLong() }
        .sorted().takeLast(2)
        .let { (first, second) -> first * second }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 10605)
    check(part2(testInput) == 2713310158)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
