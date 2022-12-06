fun main() {
    fun String.findFirstSubstringWithDifferentChars(substringLength: Int): Int {
        for (index in substringLength..length)
            if ((index - substringLength until index)
                    .map { this[it] }
                    .toSet()
                    .size == substringLength
            ) {
                return index
            }
        return length
    }

    fun part1(input: List<String>) = input.first().findFirstSubstringWithDifferentChars(4)


    fun part2(input: List<String>) = input.first().findFirstSubstringWithDifferentChars(14)


    // test if implementation meets criteria from the description, like:
    check(part1(listOf("bvwbjplbgvbhsrlpgdmjqwftvncz")) == 5)
    check(part1(listOf("nppdvjthqldpwncqszvftbrmjlhg")) == 6)
    check(part1(listOf("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")) == 10)
    check(part1(listOf("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")) == 11)

    check(part2(listOf("mjqjpqmgbljsphdztnvjfqwrcgsmlb")) == 19)
    check(part2(listOf("bvwbjplbgvbhsrlpgdmjqwftvncz")) == 23)
    check(part2(listOf("nppdvjthqldpwncqszvftbrmjlhg")) == 23)
    check(part2(listOf("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")) == 29)
    check(part2(listOf("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")) == 26)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
