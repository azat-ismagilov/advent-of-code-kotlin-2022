import kotlin.math.abs

data class Beacon(val x: Int, val y: Int) {
    fun toTuningFrequency(): Long = x * 4_000_000L + y
}

data class Sensor(val x: Int, val y: Int, val closestBeacon: Beacon) {
    fun intoRange(yLine: Int): IntRange {
        val distance = abs(x - closestBeacon.x) + abs(y - closestBeacon.y)
        val xRange = distance - abs(y - yLine)
        return x - xRange..x + xRange
    }

    companion object {
        fun of(string: String): Sensor {
            val (_, strX, strY, strBeaconX, strBeaconY) =
                Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
                    .matchEntire(string)!!.groupValues
            return Sensor(strX.toInt(), strY.toInt(), Beacon(strBeaconX.toInt(), strBeaconY.toInt()))
        }
    }
}

fun main() {
    val day = 15

    fun prepareInput(input: List<String>) = input.map { Sensor.of(it) }

    fun <T> MutableMap<T, Int>.addSafe(key: T, add: Int) {
        this[key] = this.getOrDefault(key, 0) + add
    }

    fun List<IntRange>.intersection(): Int {
        val mapPositions = sortedMapOf<Int, Int>()
        for (range in this) {
            mapPositions.addSafe(range.first, 1)
            mapPositions.addSafe(range.last, -1)
        }
        var intersectionLength = 0
        var balance = 0
        var previousIndex: Int? = null
        for ((index, balanceAdd) in mapPositions) {
            if (previousIndex == null)
                previousIndex = index
            if (balance > 0)
                intersectionLength += index - previousIndex!!
            balance += balanceAdd
            previousIndex = index
        }
        return intersectionLength
    }

    fun List<IntRange>.findEmptyCell(possiblePositions: IntRange): Int? {
        val mapPositions = sortedMapOf<Int, Int>()
        for (range in this) {
            mapPositions.addSafe(range.first, 1)
            mapPositions.addSafe(range.last + 1, -1)
        }
        mapPositions.addSafe(possiblePositions.first, 0)
        mapPositions.addSafe(possiblePositions.last, 0)
        var balance = 0
        for ((index, balanceAdd) in mapPositions) {
            balance += balanceAdd
            if (balance == 0 && index in possiblePositions)
                return index
        }
        return null
    }

    fun part1(input: List<String>, searchY: Int): Int {
        val sensors = prepareInput(input)
        val ranges = sensors.map { it.intoRange(searchY) }.filterNot { it.isEmpty() }
        return ranges.intersection()
    }

    fun part2(input: List<String>, rangeX: IntRange, rangeY: IntRange): Long {
        val sensors = prepareInput(input)
        for (searchY in rangeY) {
            val xPosition = sensors.map { it.intoRange(searchY) }.filterNot { it.isEmpty() }.findEmptyCell(rangeX)
            if (xPosition != null)
                return Beacon(xPosition, searchY).toTuningFrequency()
        }
        return 0L
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 0..20, 0..20) == 56000011L)

    val input = readInput("Day${day}")
    println(part1(input, 2000000))
    println(part2(input, 0..4_000_000, 0..4_000_000))
}
