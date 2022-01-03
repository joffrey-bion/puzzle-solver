package org.hildan.puzzle

fun List<Cell>.rotateTo(orientation: Orientation): List<Cell> = map { orientation.rotateAroundOrigin(it) }

enum class Orientation {
    BASE {
        override fun rotateAroundOrigin(p: Cell): Cell = p
    },
    ROT90 {
        override fun rotateAroundOrigin(p: Cell): Cell = Cell(-p.col, p.row)
    },
    ROT180 {
        override fun rotateAroundOrigin(p: Cell): Cell = Cell(-p.row, -p.col)
    },
    ROT270 {
        override fun rotateAroundOrigin(p: Cell): Cell = Cell(p.col, -p.row)
    };

    abstract fun rotateAroundOrigin(p: Cell): Cell
}
