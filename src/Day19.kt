sealed interface Robot

data class OreRobot(val oreRequired: Int) : Robot

data class ClayRobot(val oreRequired: Int) : Robot

data class ObsidianRobot(val oreRequired: Int, val clayRequired: Int) : Robot

data class GeodeRobot(val oreRequired: Int, val obsidianRequired: Int) : Robot

data class BlueprintState(
    val durationMinutes: Int,
    val oreRobotsCount: Int = 1,
    val clayRobotsCount: Int = 0,
    val obsidianRobotsCount: Int = 0,
    val availableOre: Int = 0,
    val availableClay: Int = 0,
    val availableObsidian: Int = 0
)


data class Blueprint(
    val id: Int,
    val oreRobot: OreRobot,
    val clayRobot: ClayRobot,
    val obsidianRobot: ObsidianRobot,
    val geodeRobot: GeodeRobot
) {
    private val savedStates = mutableMapOf<BlueprintState, Int>()

    private fun maxNeededOre() = maxOf(
        oreRobot.oreRequired, clayRobot.oreRequired, obsidianRobot.oreRequired, geodeRobot.oreRequired
    )

    private fun maxNeededClay() = obsidianRobot.clayRequired

    private fun maxNeededObsidian() = geodeRobot.obsidianRequired

    private fun mostAmountOfGeode(state: BlueprintState): Int {
        if (state.durationMinutes == 0) return 0

        if (state in savedStates) return savedStates[state]!!

        var answer = 0
        for (newRobot in listOf(oreRobot, clayRobot, obsidianRobot, geodeRobot, null)) {
            var newOreCount = minOf(
                state.availableOre,
                state.durationMinutes * (maxNeededOre() - state.oreRobotsCount) + maxNeededOre()
            )
            var newClayCount = minOf(
                state.availableClay,
                state.durationMinutes * (maxNeededClay() - state.clayRobotsCount) + maxNeededClay()
            )
            var newObsidianCount = minOf(
                state.availableObsidian,
                state.durationMinutes * (maxNeededObsidian() - state.obsidianRobotsCount) + maxNeededObsidian()
            )

            var newOreRobotsCount = minOf(state.oreRobotsCount, maxNeededOre())
            var newClayRobotsCount = minOf(state.clayRobotsCount, maxNeededClay())
            var newObsidianRobotsCount = minOf(state.obsidianRobotsCount, maxNeededObsidian())

            when (newRobot) {
                is OreRobot -> {
                    newOreCount -= newRobot.oreRequired
                    newOreRobotsCount++
                }

                is ClayRobot -> {
                    newOreCount -= newRobot.oreRequired
                    newClayRobotsCount++
                }

                is ObsidianRobot -> {
                    newOreCount -= newRobot.oreRequired
                    newClayCount -= newRobot.clayRequired
                    newObsidianRobotsCount++
                }

                is GeodeRobot -> {
                    newOreCount -= newRobot.oreRequired
                    newObsidianCount -= newRobot.obsidianRequired
                }

                else -> {}
            }

            if (listOf(newOreCount, newClayCount, newObsidianCount).any { it < 0 }) continue

            newOreCount += state.oreRobotsCount
            newClayCount += state.clayRobotsCount
            newObsidianCount += state.obsidianRobotsCount

            val producedGeode = if (newRobot is GeodeRobot) state.durationMinutes - 1 else 0

            answer = maxOf(
                answer, producedGeode + mostAmountOfGeode(
                    BlueprintState(
                        state.durationMinutes - 1,
                        newOreRobotsCount,
                        newClayRobotsCount,
                        newObsidianRobotsCount,
                        newOreCount,
                        newClayCount,
                        newObsidianCount
                    )
                )
            )
        }
        savedStates[state] = answer
        return answer
    }

    fun mostAmountOfGeode(timeMinutes: Int): Int {
        val result = mostAmountOfGeode(BlueprintState(timeMinutes))
        savedStates.clear()
        return result
    }

    companion object {
        fun of(string: String): Blueprint {
            val regexPatterns = listOf(
                Regex("Blueprint (\\d+):"),
                Regex("Each ore robot costs (\\d+) ore."),
                Regex("Each clay robot costs (\\d+) ore."),
                Regex("Each obsidian robot costs (\\d+) ore and (\\d+) clay."),
                Regex("Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
            )
            val matches = regexPatterns.map { pattern ->
                pattern.find(string)!!.groupValues.drop(1).map { it.toInt() }
            }

            return Blueprint(
                matches[0][0],
                OreRobot(matches[1][0]),
                ClayRobot(matches[2][0]),
                ObsidianRobot(matches[3][0], matches[3][1]),
                GeodeRobot(matches[4][0], matches[4][1])
            )
        }
    }
}

fun main() {
    val day = 19

    fun prepareInput(input: List<String>) = input.map { Blueprint.of(it) }

    fun part1(input: List<String>): Int {
        val blueprints = prepareInput(input)

        return blueprints.sumOf { blueprint ->
            blueprint.id * blueprint.mostAmountOfGeode(24)
        }
    }

    fun part2(input: List<String>): Int {
        val blueprints = prepareInput(input)

        return blueprints.take(3).map { blueprint ->
            blueprint.mostAmountOfGeode(32)
        }.let { (a, b, c) -> a * b * c }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 33)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
