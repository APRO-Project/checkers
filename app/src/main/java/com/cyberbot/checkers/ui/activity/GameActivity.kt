package com.cyberbot.checkers.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyberbot.checkers.R
import com.cyberbot.checkers.fx.Sound
import com.cyberbot.checkers.fx.SoundType
import com.cyberbot.checkers.game.AiPlayer
import com.cyberbot.checkers.game.Grid
import com.cyberbot.checkers.game.GridEntry
import com.cyberbot.checkers.game.PlayerNum
import com.cyberbot.checkers.preferences.Preferences
import com.cyberbot.checkers.ui.view.MoveAttemptListener
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.max


class GameActivity : AppCompatActivity() {

    companion object {
        val GRID_STATE_KEY = "grid"
        val TURN_KEY = "grid"
    }

    private lateinit var aiPlayer: AiPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        move_player2.text = getString(R.string.game_player_turn_info)

        val pref = Preferences.fromContext(this)
        if (savedInstanceState != null) {
            savedInstanceState.run {
                checkersGridView.gridData = getSerializable(GRID_STATE_KEY) as Grid
                checkersGridView.playerTurn = getSerializable(TURN_KEY) as PlayerNum
            }
        } else {
            checkersGridView.gridData = Grid.fromPreferences(pref)
            checkersGridView.playerTurn = PlayerNum.SECOND
        }

        checkersGridView.captureHints = pref.captureHints
        aiPlayer = AiPlayer(PlayerNum.FIRST, PlayerNum.SECOND, pref.aiDepth)

        checkersGridView.moveAttemptListener = object : MoveAttemptListener {
            override fun onForcedMoveStart(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry) {
                move_player2.text = getString(R.string.game_player_move_in_progress)
            }

            override fun onForcedMoveEnd(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry) {
                grid.attemptMove(srcEntry, dstEntry)
                checkersGridView.playerTurn = PlayerNum.SECOND
                move_player2.text = getString(R.string.game_player_turn_info)
            }

            override fun onUserMoveStart(grid: Grid, srcEntry: GridEntry) {

            }

            override fun onUserMoveEnd(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry) {
                if (srcEntry == dstEntry) {
                    return
                }

                grid.attemptMove(srcEntry, dstEntry)
                if (dstEntry.player == PlayerNum.SECOND) {
                    executeAiMove()
                }
            }
        }
    }

    private fun executeAiMove() {
        checkersGridView.playerTurn = PlayerNum.NOPLAYER
        move_player2.text = getString(R.string.game_ai_thinking)
        Thread {
            Sound.playSound(this@GameActivity, SoundType.AI_THINK)

            val startThinking = System.currentTimeMillis()
            aiPlayer.executeMove(checkersGridView.gridData)
            val endThinking = System.currentTimeMillis()

            Thread.sleep(max(0, 1000 - (endThinking - startThinking)))
            runOnUiThread {
                val src = aiPlayer.aiMoveSource
                val dst = aiPlayer.aiMoveDestination.destinationEntry
                checkersGridView.animateMove(src, dst)
            }
        }.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putSerializable(GRID_STATE_KEY, checkersGridView.gridData)
            putSerializable(TURN_KEY, checkersGridView.playerTurn)
        }

        super.onSaveInstanceState(outState)
    }
}