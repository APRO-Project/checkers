package com.cyberbot.checkers.preferences

import android.content.Context
import com.cyberbot.checkers.R

class Preferences private constructor(
    var gridSize: Int,
    var playerRows: Int,
    var canMoveBackwards: Boolean,
    var canCaptureBackwards: Boolean,
    var flyingKing: Boolean
) {
    companion object {
        fun fromContext(context: Context): Preferences {
            context.apply {
                val sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                ) ?: throw RuntimeException("Unable to get preferences")

                val gridSize = sharedPref.getInt(getString(R.string.preference_grid_size), 10)
                val playerRows = sharedPref.getInt(getString(R.string.preference_player_rows), 4)
                val canMoveBackwards =
                    sharedPref.getBoolean(getString(R.string.preference_can_move_backwards), false)
                val canCaptureBackwards =
                    sharedPref.getBoolean(
                        getString(R.string.preference_can_capture_backwards),
                        false
                    )
                val flyingKing =
                    sharedPref.getBoolean(getString(R.string.preference_flying_king), false)

                return Preferences(
                    gridSize,
                    playerRows,
                    canMoveBackwards,
                    canCaptureBackwards,
                    flyingKing
                )
            }
        }
    }

    fun save(context: Context) {
        context.apply {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            ) ?: throw RuntimeException("Unable to get preferences")

            sharedPref.edit().also {
                it.putInt(getString(R.string.preference_grid_size), gridSize)
                it.putInt(getString(R.string.preference_player_rows), playerRows)
                it.putBoolean(getString(R.string.preference_can_move_backwards), canMoveBackwards)
                it.putBoolean(getString(R.string.preference_can_capture_backwards), canCaptureBackwards)
                it.putBoolean(getString(R.string.preference_flying_king), flyingKing)
                it.apply()
            }
        }
    }
}