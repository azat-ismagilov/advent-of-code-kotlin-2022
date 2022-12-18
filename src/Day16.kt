fun main() {
    val day = 16

    data class Valve(val id: Int, val flowRate: Int, val nextValves: List<Int>)

    fun prepareInput(input: List<String>): List<Valve> {
        val grouping = input.map {
            Regex("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)")
                .matchEntire(it)!!
                .groupValues
                .drop(1)
        }
        val nameToId = grouping.map { it.first() }
            .sorted()
            .mapIndexed { index, name -> name to index }
            .toMap()
        return grouping.map { (valveName, valveFlowRateStr, nextValvesStr) ->
            val valveId = nameToId[valveName]!!
            val valveFlowRate = valveFlowRateStr.toInt()
            val nextValvesId = nextValvesStr.split(", ").map { otherValveName -> nameToId[otherValveName]!! }
            Valve(valveId, valveFlowRate, nextValvesId)
        }
    }

    fun <K> MutableMap<K, Int>.maxOrSet(k: K, v: Int) {
        val prevValue = this[k]
        if (prevValue == null || prevValue < v)
            this[k] = v
    }

    fun part1(input: List<String>, maxTimeMinutes: Int): Int {
        val valves = prepareInput(input)

        //dp[currentTimeMinutes][valveId][maskOfOpenedValves] = maximum score
        val dp = List(maxTimeMinutes + 1) {
            List(valves.size) { mutableMapOf<ULong, Int>() }
        }
        dp[0][0][0UL] = 0

        for (time in 0 until maxTimeMinutes)
            for (valve in valves)
                for ((mask, score) in dp[time][valve.id]) {
                    val valveBit = (1UL shl valve.id)
                    if (valve.flowRate > 0 && (mask and valveBit) == 0UL)
                        dp[time + 1][valve.id].maxOrSet(
                            mask or valveBit,
                            score + (maxTimeMinutes - time - 1) * valve.flowRate
                        )
                    for (nextValveId in valve.nextValves)
                        dp[time + 1][nextValveId].maxOrSet(mask, score)
                }


        return dp[maxTimeMinutes].maxOf { it.maxOfOrNull { (_, v) -> v } ?: 0 }
    }

    fun part2(input: List<String>, maxTimeMinutes: Int): Int {
        val valves = prepareInput(input)

        //dp[currentTimeMinutes][valveId][maskOfOpenedValves] = maximum score
        val dp = List(maxTimeMinutes + 1) {
            List(valves.size) { mutableMapOf<ULong, Int>() }
        }
        dp[0][0][0UL] = 0

        for (time in 0 until maxTimeMinutes)
            for (valve in valves)
                for ((mask, score) in dp[time][valve.id]) {
                    val valveBit = (1UL shl valve.id)
                    if (valve.flowRate > 0 && (mask and valveBit) == 0UL)
                        dp[time + 1][valve.id].maxOrSet(
                            mask or valveBit,
                            score + (maxTimeMinutes - time - 1) * valve.flowRate
                        )
                    for (nextValveId in valve.nextValves)
                        dp[time + 1][nextValveId].maxOrSet(mask, score)
                }

        val possibleMasks = mutableMapOf<ULong, Int>()
        for ((mask, score) in dp[maxTimeMinutes].flatMap { it.toList() })
            possibleMasks.maxOrSet(mask, score)

        return possibleMasks.maxOf { (mask, score) ->
            score + possibleMasks.maxOf { (elephantMask, elephantScore) ->
                if (elephantMask and mask == 0UL) elephantScore else 0
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput, 30) == 1651)
    check(part2(testInput, 26) == 1707)

    val input = readInput("Day${day}")
    println(part1(input, 30))
    println(part2(input, 26))
}
