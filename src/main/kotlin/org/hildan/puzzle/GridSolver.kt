package org.hildan.puzzle

import java.util.*

// pieces are always placed in the same order, so we could get rid of the color information and just use GridMasks here
typealias Solution = List<PlacedPiece>

data class PlacedPiece(val color: Color, val gridMask: GridMask)

class GridSolver(piecesSide: Iterable<PieceSide>) {
    private var grid = EMPTY_GRID

    // we could save some time by moving an index on this list instead of removing and re-adding elements
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
