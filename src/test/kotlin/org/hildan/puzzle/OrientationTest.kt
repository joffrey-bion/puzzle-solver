package org.hildan.puzzle

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OrientationTest {

    @Test
    fun rotate_base() {
        val cells = listOf(
            Cell(2, 5),
            Cell(3, 5),
            Cell(4, 5),
        )
        val rotated = cells.rotateTo(Orientation.BASE)
        assertEquals(cells, rotated)
    }

    @Test
    fun rotate_90() {
        val cells = listOf(
            Cell(2, 2),
            Cell(3, 2),
            Cell(4, 2),
        )
        val expectedRotated = listOf(
            Cell(-2, 2),
            Cell(-2, 3),
            Cell(-2, 4),
        )
        val rotated = cells.rotateTo(Orientation.ROT90)
        assertEquals(expectedRotated, rotated)
    }

    @Test
    fun rotate_180() {
        val cells = listOf(
            Cell(2, 5),
            Cell(3, 5),
            Cell(4, 5),
        )
        val expectedRotated = listOf(
            Cell(-2, -5),
            Cell(-3, -5),
            Cell(-4, -5),
        )
        val rotated = cells.rotateTo(Orientation.ROT180)
        assertEquals(expectedRotated, rotated)
    }

    @Test
    fun rotate_90_clockwise() {
        val cells = listOf(
            Cell(2, 2),
            Cell(3, 2),
            Cell(4, 2),
        )
        val expectedRotated = listOf(
            Cell(2, -2),
            Cell(2, -3),
            Cell(2, -4),
        )
        val rotated = cells.rotateTo(Orientation.ROT270)
        assertEquals(expectedRotated, rotated)
    }
}
