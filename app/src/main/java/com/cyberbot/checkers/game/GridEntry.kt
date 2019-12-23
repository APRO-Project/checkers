package com.cyberbot.checkers.game

class GridEntry(val x: Int, val y: Int) {
    var player: PlayerNum = PlayerNum.NOPLAYER

    fun legal() = (x % 2 xor y % 2) > 0

    override fun toString(): String {
        return "($x, $y) - $player"
    }
}

enum class PlayerNum {
    NOPLAYER,
    FIRST,
    SECOND
}