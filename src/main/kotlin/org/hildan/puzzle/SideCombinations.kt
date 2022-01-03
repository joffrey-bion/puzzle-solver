package org.hildan.puzzle

class SideCombinationsSolver(allPieces: List<Piece>) {
    private val piecesToPlace = allPieces.toMutableList()
    private val placedPieces = mutableListOf<PieceSide>()
    private val validCombinations = mutableListOf<List<PieceSide>>()

    /**
     * Find the valid combinations of piece sides that add up to the grid size.
     * It's pointless to even try to fit pieces that don't cover the whole grid, or are too big.
     */
    fun findCombinations(nGridSlots: Int): List<List<PieceSide>> {
        findCombinationsRecursively(nGridSlots)
        return validCombinations
    }

    private fun findCombinationsRecursively(nEmptyCells: Int) {
        if (piecesToPlace.isEmpty()) {
            if (nEmptyCells == 0) {
                validCombinations.add(placedPieces.toList())
            }
            return
        }
        if (nEmptyCells <= 0) {
            return
        }
        val piece = piecesToPlace.removeLast()

        piece.sides.forEach { side ->
            if (side.nCells <= nEmptyCells) {
                placedPieces.add(side)
                findCombinationsRecursively(nEmptyCells - side.nCells)
                placedPieces.remove(side)
            }
        }
        piecesToPlace.add(piece)
    }
}
