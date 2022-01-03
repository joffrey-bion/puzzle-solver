package org.hildan.puzzle

/**
 * Finds all possible placements for this group of cells by translating and rotating them in all directions.
 */
// This doesn't need to be efficient, as it's only called during initialization
fun List<Cell>.allPlacementsAndRotations(): GridMasks =
    Orientation.values().flatMap { rotateTo(it).allPlacements() }.toGridMasks()

private fun List<Cell>.allPlacements(): List<GridMask> {
    val points = this
    val minRow = points.minOf { it.row }
    val maxRow = points.maxOf { it.row }
    val minCol = points.minOf { it.col }
    val maxCol = points.maxOf { it.col }

    // we want to place the min at 0 and the max at <GRID_SIZE> - 1
    val startRowOffset = -minRow
    val endRowOffset = GRID_HEIGHT - 1 - maxRow
    val startColOffset = -minCol
    val endColOffset = GRID_WIDTH - 1 - maxCol

    return (startRowOffset..endRowOffset).flatMap { rowOffset ->
        (startColOffset..endColOffset).map { colOffset ->
            points.map { it.translate(rowOffset, colOffset) }.toGridMask()
        }
    }
}

private fun Cell.translate(rowOffset: Int, colOffset: Int) = Cell(row + rowOffset, col + colOffset)
