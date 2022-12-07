fun main() {

    abstract class FileSystemUnit() {
        abstract val size: Int
    }

    class Directory(val parent: Directory? = null) : FileSystemUnit() {
        private var realSize: Int? = null
        override val size: Int
            get() {
                if (realSize == null)
                    realSize = contents.values.sumOf { it.size }
                return realSize!!
            }
        val contents = mutableMapOf<String, FileSystemUnit>()

        fun listOfSubdirs(): List<Directory> = contents.filter { it.value is Directory }
            .flatMap { (it.value as Directory).listOfSubdirs() } + listOf<Directory>(this)
    }

    class File(override val size: Int) : FileSystemUnit()

    fun calculateDirectoriesSizes(input: List<String>): List<Directory> {
        val rootDirectory = Directory()
        var currentDirectory = rootDirectory

        val inputIterator = input.listIterator()
        while (inputIterator.hasNext()) {
            val command = inputIterator.next().split(' ').drop(1)

            when (command[0]) {
                "ls" -> {
                    while (inputIterator.hasNext() && !input[inputIterator.nextIndex()].startsWith("$ ")) {
                        val line = inputIterator.next().split(' ')
                        val fileSystemName = line[1]
                        currentDirectory.contents.putIfAbsent(
                            fileSystemName, when (line[0]) {
                                "dir" -> Directory(currentDirectory)
                                else -> File(line[0].toInt())
                            }
                        )
                    }
                }

                "cd" -> {
                    val path = command[1]
                    currentDirectory = when (path) {
                        "/" -> rootDirectory
                        ".." -> currentDirectory.parent ?: rootDirectory
                        else -> {
                            currentDirectory.contents.putIfAbsent(path, Directory(currentDirectory))
                            currentDirectory.contents[path] as Directory
                        }
                    }
                }
            }
        }

        return rootDirectory.listOfSubdirs()
    }

    fun part1(input: List<String>) =
        calculateDirectoriesSizes(input)
            .filter { it.size <= 100_000 }
            .sumOf { it.size }

    fun part2(input: List<String>) =
        calculateDirectoriesSizes(input).let { directories ->
            val totalSize = directories.maxOf { it.size }
            val neededFreeSpace = 30_000_000 - (70_000_000 - totalSize)
            directories.filter { it.size >= neededFreeSpace }.minOf { it.size }
        }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
