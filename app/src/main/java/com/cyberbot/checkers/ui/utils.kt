package com.cyberbot.checkers.ui

import android.content.Context
import androidx.annotation.StringRes
import com.cyberbot.checkers.R
import com.cyberbot.checkers.game.GameEndReason

fun Context.getEndGameString(gameEndReason: GameEndReason, positive: Boolean): String {
    return getString(getStringResFromReason(gameEndReason, positive))
}

@StringRes
fun getStringResFromReason(gameEndReason: GameEndReason, positive: Boolean): Int {
    return if (positive) {
        when (gameEndReason) {
            GameEndReason.DRAW_TOO_MANY_KING_ONLY_MOVES -> R.string.game_end_draw_too_many_king_only
            GameEndReason.DRAW_NO_MOVABLE_PIECES_REMAINING -> R.string.game_end_draw_no_movable_remaining
            GameEndReason.DRAW_KING_VS_KING -> R.string.game_end_draw_king_vs_king
            GameEndReason.WIN_OPPONENT_NO_PIECES_REMAINING -> R.string.game_end_positive_no_pieces_remaining
            GameEndReason.WIN_OPPONENT_NO_MOVABLE_PIECES_REMAINING -> R.string.game_end_negative_no_movable_remaining
        }
    } else {
        when (gameEndReason) {
            GameEndReason.DRAW_TOO_MANY_KING_ONLY_MOVES -> R.string.game_end_draw_too_many_king_only
            GameEndReason.DRAW_NO_MOVABLE_PIECES_REMAINING -> R.string.game_end_draw_no_movable_remaining
            GameEndReason.DRAW_KING_VS_KING -> R.string.game_end_draw_king_vs_king
            GameEndReason.WIN_OPPONENT_NO_PIECES_REMAINING -> R.string.game_end_negative_no_pieces_remaining
            GameEndReason.WIN_OPPONENT_NO_MOVABLE_PIECES_REMAINING -> R.string.game_end_negative_no_movable_remaining
        }
    }
}