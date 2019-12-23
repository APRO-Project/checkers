package com.cyberbot.checkers.game

class GridEntry(val x: Int, val y: Int) {
    var player: PlayerNum = PlayerNum.NOPLAYER

    fun legal() = (x % 2 xor y % 2) > 0

    override fun toString(): String {
        return "($x, $y) - $player"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GridEntry

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }


}

enum class PlayerNum {
    NOPLAYER,
    FIRST,
    SECOND
}