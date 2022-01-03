package org.hildan.puzzle

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.writeText

private const val SOLUTIONS_DIR = "solutions"
private const val OUTPUT_AS_PNG = true
private val EXTENSION = if (OUTPUT_AS_PNG) "png" else "txt"

fun main(): Unit = runBlocking(Dispatchers.Default) {
    println("Finding valid choices for the sides of the pieces...")
    val validPieceCombinations = SideCombinationsSolver(Piece.ALL).findCombinations(N_GRID_SLOTS)
    println("Found ${validPieceCombinations.size} unique combinations of piece sides covering the whole grid")

    println("Searching solutions for each combination of sides...")
    val solutionsChannel = launchSolversInParallel(validPieceCombinations)

    withContext(Dispatchers.IO) {
        var nSolutions = 0
        solutionsChannel.consumeEach { s ->
            nSolutions++

            writeSolutionFile(s, index = nSolutions)

            if (nSolutions % 10 == 0) {
                print("\r$nSolutions unique solutions found so far")
            }
        }
        println("\rDone! $nSolutions unique solutions written to directory '$SOLUTIONS_DIR'")
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

private fun writeSolutionFile(solution: Solution, index: Int) {
    // using the index because 5204 hashes have duplicates (out of 301350 solutions)
    val name = "solution-$index-${solution.hashCode().toUInt().toString(16)}"
    val filename = "$SOLUTIONS_DIR/$name.$EXTENSION"
    if (OUTPUT_AS_PNG) {
        ImageIO.write(solution.toImage(), "png", File(filename))
    } else {
        Path(filename).writeText(solution.toGridString())
    }
}

private fun Solution.toGridString() = buildString {
    (0 until GRID_HEIGHT).forEach { row ->
        (0 until GRID_WIDTH).forEach { col ->
            val placedPieceHere = this@toGridString.firstOrNull { placedPiece ->
                Cell(row, col).isInMask(placedPiece.gridMask)
            }
            append(placedPieceHere?.color?.ordinal?.digitToChar() ?: '.')
        }
        appendLine()
    }
}
