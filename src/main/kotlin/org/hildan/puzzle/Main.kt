package org.hildan.puzzle

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import javax.imageio.ImageIO
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeLines

private const val OUTPUT_AS_PNG = true

private val solutionsDir = createTempDirectory("solutions-")
private val solutionsExt = if (OUTPUT_AS_PNG) "png" else "txt"

fun main(): Unit = runBlocking {
    println("Finding valid choices for the sides of the pieces...")
    val validPieceCombinations = SideCombinationsSolver(Piece.ALL).findCombinations(N_GRID_SLOTS)
    println("Found ${validPieceCombinations.size} unique combinations of piece sides covering the whole grid")

    println("Will write solution files to '$solutionsDir'")
    println("Searching solutions for each combination of sides...")
    val solutionsChannel = launchSolversInParallel(validPieceCombinations)

    withContext(Dispatchers.IO) {
        var nSolutions = 0
        solutionsChannel.consumeEach { s ->
            nSolutions++

            s.writeFile()

            if (nSolutions % 16 == 0) {
                print("\r$nSolutions unique solutions found so far")
            }
        }
        println("\rDone! $nSolutions unique solutions found")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun CoroutineScope.launchSolversInParallel(validPieceCombinations: List<List<PieceSide>>): ReceiveChannel<Solution> =
    produce(Dispatchers.Default, capacity = Channel.UNLIMITED) {
        validPieceCombinations.forEach { sidedPieces ->
            launch {
                GridSolver(sidedPieces).forEachSolution { s ->
                    // never blocks thanks to unlimited channel capacity (I/O can keep up, apparently)
                    trySendBlocking(s)
                }
            }
        }
    }

private fun Solution.writeFile() {
    val solutionTextLines = asTextLines()
    val solutionString = solutionTextLines.joinToString("-")
    val solutionPath = solutionsDir.resolve("solution-$solutionString.$solutionsExt")
    if (OUTPUT_AS_PNG) {
        ImageIO.write(toImage(), "png", solutionPath.toFile())
    } else {
        solutionPath.writeLines(solutionTextLines)
    }
}

private fun Solution.asTextLines() = (0 until GRID_HEIGHT).map { row ->
    (0 until GRID_WIDTH).joinToString("") { col ->
        pieceAt(row, col)?.color?.ordinal?.digitToChar()?.toString() ?: "."
    }
}

private fun Solution.pieceAt(row: Int, col: Int) = firstOrNull { placedPiece ->
    Cell(row, col).isInMask(placedPiece.gridMask)
}
