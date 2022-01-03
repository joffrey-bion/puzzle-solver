package org.hildan.puzzle

class PieceSide(
    val color: Color,
    val nCells: Int,
    val allPlacements: GridMasks,
)

class Piece(
    private val color: Color,
    val sides: List<PieceSide>,
) {
    constructor(color: Color, bigSide: String, smallSide: String): this(
        color = color,
        sides = listOf(
            bigSide.toPieceSide(color),
            smallSide.toPieceSide(color),
        )
    )

    override fun toString(): String = "Piece($color)"

    companion object {

        val ALL = listOf(
            Piece(
                color = Color.RED,
                bigSide = """
                    xxxx
                    x..x
                """.trimIndent(),
                smallSide= """
                    xxxx
                    x...
                """.trimIndent(),
            ),
            Piece(
                color = Color.BLUE,
                bigSide = """
                    xxxx
                    .x.x
                """.trimIndent(),
                smallSide= """
                    xxxx
                    x...
                """.trimIndent(),
            ),
            Piece(
                color = Color.ORANGE,
                bigSide = """
                    xxxx
                    x.x.
                """.trimIndent(),
                smallSide = """
                    xxxx
                    .x..
                """.trimIndent(),
            ),
            Piece(
                color = Color.PINK,
                bigSide = """
                    xxxx
                    xx..
                """.trimIndent(),
                smallSide= """
                    xxxx
                    ..x.
                """.trimIndent(),
            ),
            Piece(
                color = Color.LIGHT_BLUE,
                bigSide = """
                    xxxx
                    .xx.
                """.trimIndent(),
                smallSide= """
                    xxxx
                    .x..
                """.trimIndent(),
            ),
            Piece(
                color = Color.YELLOW,
                bigSide = """
                    xxxx
                    ..xx
                """.trimIndent(),
                smallSide= """
                    xxxx
                    ...x
                """.trimIndent(),
            ),
            Piece(
                color = Color.PURPLE,
                bigSide = """
                    xxx
                    .xx
                """.trimIndent(),
                smallSide= """
                    xxx
                    ..x
                """.trimIndent(),
            ),
            Piece(
                color = Color.DARK_TEAL,
                bigSide = """
                    xxx
                    xx.
                """.trimIndent(),
                smallSide= """
                    xxx
                    .x.
                """.trimIndent(),
            ),
            Piece(
                color = Color.DARK_BLUE,
                bigSide = """
                    xxx
                    x.x
                """.trimIndent(),
                smallSide= """
                    xxx
                    .x.
                """.trimIndent(),
            ),
            Piece(
                color = Color.GREEN,
                bigSide = """
                    xxx
                    x.x
                """.trimIndent(),
                smallSide= """
                    xxx
                    x..
                """.trimIndent(),
            ),
        )
    }
}

private fun String.toPieceSide(color: Color): PieceSide {
    val cells = shapeToCells()
    return PieceSide(color, nCells = cells.size, cells.allPlacementsAndRotations())
}

private fun String.shapeToCells(): List<Cell> = lines().flatMapIndexed { row, chars ->
    chars.mapIndexedNotNull { col, char ->
        when (char) {
            'x' -> Cell(row, col)
            '.' -> null
            else -> error("unsupported piece character '$char'")
        }
    }
}
