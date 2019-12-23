package com.cyberbot.checkers.game

import kotlin.math.pow

class Grid(val size: Int = 8, private val playerRows: Int = 3) : Iterable<GridEntry> {
    private val gridEntries: Array<GridEntry>

    init {
        gridEntries = Array(size.toDouble().pow(2).toInt()) {
            val y = it / size
            val entry = GridEntry(it % size, y)

            if (y < playerRows && entry.legal()) {
                entry.player = PlayerNum.FIRST
            } else if (y >= size - playerRows && entry.legal()) {
                entry.player = PlayerNum.SECOND
            }

            return@Array entry
        }
    }

    override fun iterator(): Iterator<GridEntry> {
        return gridEntries.iterator()
    }
}