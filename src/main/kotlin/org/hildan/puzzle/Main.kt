package org.hildan.puzzle

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.trySendBlocking
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.writeText

fun main(): Unit = runBlocking(Dispatchers.Default) {
    println("Finding valid choices for the sides of the pieces...")
    val validPieceCombinations = SideCombinationsSolver(Piece.ALL).findCombinations(N_GRID_SLOTS)
    println("Found ${validPieceCombinations.size} unique combinations of piece sides covering the whole grid")

    val solutions = Channel<Solution>(Channel.UNLIMITED)

    launch(Dispatchers.IO) {
        var nSolutions = 0
        solutions.consumeEach { s ->
            nSolutions++
            writeImageFile(s, index = nSolutions)
            print("\r$nSolutions unique solutions found so far")
        }
        println("\rDone! $nSolutions unique solutions written to ./solutions")
    }

    coroutineScope {
        println("Searching solutions for each combination of sides...")
        validPieceCombinations.forEach { sidedPieces ->
            launch {
                GridSolver(sidedPieces).forEachSolution {
                    solutions.trySendBlocking(it)
                }
            }
        }
    }
    solutions.close()
}

private fun writeImageFile(solution: Solution, index: Int) {
    val filename = "solutions/solution-$index-${solution.hashCode().toUInt().toString(16)}.png"
    ImageIO.write(solution.toImage(), "png", File(filename))
}

typealias Solution = List<PlacedPiece>

data class PlacedPiece(val color: Color, val gridMask: GridMask)

class GridSolver(piecesSide: Iterable<PieceSide>) {
    private var grid = EMPTY_GRID
    private val piecesToPlace = piecesSide.toMutableList()

    // This auto-boxes GridMask's Long, and could be improved using a LongArray instead of a Map.
    // Indices in the array could be the ordinal of the Color class, so we don't even have to store the color.
    // Values could be non-nullable since we could use 0 as
    private val placedPieces = EnumMap<Color, GridMask>(Color::class.java)

    fun forEachSolution(action: (Solution) -> Unit) {
        if (piecesToPlace.isEmpty()) {
            check(grid.isFull()) {
                "No more pieces but the grid is not full:\n${grid.toString(16)}\n$grid"
            }
            val solution = buildCurrentSolution()
            action(solution)
            return
        }
        val sidedPiece = piecesToPlace.removeLast()

        sidedPiece.allPlacements.forEach { pl ->
            if (grid.canFit(pl)) {
                placePiece(sidedPiece.color, pl)
                forEachSolution(action)
                removePiece(sidedPiece.color, pl)
            }
        }
        piecesToPlace.add(sidedPiece)
    }

    private fun buildCurrentSolution() = placedPieces.entries.map { PlacedPiece(it.key, it.value) }

    private fun placePiece(color: Color, pl: GridMask) {
        placedPieces[color] = pl
        grid = grid.withPiece(pl)
    }

    private fun removePiece(color: Color, pl: GridMask) {
        grid = grid.withoutPiece(pl)
        placedPieces.remove(color)
    }
}

private fun writeTextFile(solution: Solution, index: Int) {
    val filename = "solutions/solution-$index-${solution.hashCode().toUInt().toString(16)}.txt"
    Path(filename).writeText(solution.toGridString())
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
