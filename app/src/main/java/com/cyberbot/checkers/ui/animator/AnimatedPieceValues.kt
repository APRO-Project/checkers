package com.cyberbot.checkers.ui.animator

/**
 * Wrapper class holding properties of an animated Piece.
 * It does not reference a GridEntry, so it probably should be used in a Collection that
 * allows keys and values (ex. HashMap).
 *
 * @property x The horizontal component of the position.
 * @property y The vertical component of the position.
 * @property scale The scale of the animated piece.
 */
class AnimatedPieceValues (var x: Float, var y : Float, var scale : Float) {
    override fun toString(): String {
        return "AnimatedPieceValues(x=$x, y=$y, scale=$scale)"
    }
}