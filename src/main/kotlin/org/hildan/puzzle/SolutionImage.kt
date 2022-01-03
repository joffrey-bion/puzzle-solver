package org.hildan.puzzle

import java.awt.Graphics2D
import java.awt.image.BufferedImage

private const val DOT_DIAMETER = 30

private const val IMG_WIDTH = DOT_DIAMETER * GRID_WIDTH
private const val IMG_HEIGHT = DOT_DIAMETER * GRID_HEIGHT

fun Solution.toImage(): BufferedImage = BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB).apply {
    with(createGraphics()) {
        fillBackground()
        forEach { placedPiece ->
            drawPiece(placedPiece)
        }
    }
}

private fun Graphics2D.fillBackground() {
    color = java.awt.Color.BLACK
    fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT)
}

private fun Graphics2D.drawPiece(placedPiece: PlacedPiece) {
    placedPiece.gridMask.shapeToCells().forEach { point ->
        drawDot(point, placedPiece.color)
    }
}

private fun Graphics2D.drawDot(cell: Cell, color: Color) {
    val topLeftX = cell.col * DOT_DIAMETER
    val topLeftY = cell.row * DOT_DIAMETER

    setColor(color.toJavaColor())
    fillOval(topLeftX, topLeftY, DOT_DIAMETER, DOT_DIAMETER)
}

private fun Color.toJavaColor() = java.awt.Color(argb.toInt())
