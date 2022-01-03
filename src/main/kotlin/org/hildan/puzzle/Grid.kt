package org.hildan.puzzle

const val GRID_WIDTH = 10
const val GRID_HEIGHT = 5
const val N_GRID_SLOTS = GRID_WIDTH * GRID_HEIGHT

const val EMPTY_GRID: GridMask = 0L

/** A bit mask for a full grid: 14 zeroes and 50 ones (grid is 5x10) */
private const val FULL_GRID: GridMask = 0x3ffffffffffffL

data class Cell(val row: Int, val col: Int)

/** Type alias for a bit mask of the whole grid */
typealias GridMask = Long

typealias GridMasks = LongArray

fun Iterable<Cell>.toGridMask(): GridMask = fold(0L) { bits, p -> bits or p.toGridMask()}

private fun Cell.toGridMask(): GridMask = 1L shl (col + row * GRID_WIDTH)

fun GridMask.shapeToCells(): List<Cell> =
    (0 until GRID_HEIGHT).flatMap { row ->
        (0 until GRID_WIDTH).mapNotNull { col ->
            Cell(row, col).takeIf { it.isInMask(this) }
        }
    }

fun Cell.isInMask(bitMask: GridMask): Boolean = toGridMask() and bitMask > 0L

fun GridMask.isFull(): Boolean = this == FULL_GRID

fun GridMask.canFit(piecePlacement: GridMask): Boolean = this and piecePlacement == 0L

fun GridMask.withPiece(piecePlacement: GridMask) = this or piecePlacement

fun GridMask.withoutPiece(piecePlacement: GridMask) = this and piecePlacement.inv()

fun List<GridMask>.toGridMasks(): GridMasks = toLongArray()
