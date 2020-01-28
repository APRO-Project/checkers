package com.cyberbot.checkers.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.cyberbot.checkers.R
import com.cyberbot.checkers.fx.Sound
import com.cyberbot.checkers.fx.SoundType
import com.cyberbot.checkers.game.*
import com.cyberbot.checkers.preferences.Preferences
import com.cyberbot.checkers.ui.getEndGameString
import com.cyberbot.checkers.ui.view.MoveAttemptListener
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.max


class GameActivity : AppCompatActivity() {

    companion object {
        val GRID_STATE_KEY = "grid"
        val TURN_KEY = "grid"
    }

    private lateinit var aiPlayer: AiPlayer
    private var gameEnded = false

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
                val gameOver = grid.isGameOver
                if (gameOver != null) {
                    move_player2.text = ""
                    handleEndGame(gameOver)
                    return
                }

                move_player2.text = getString(R.string.game_player_turn_info)

                checkersGridView.playerTurn = PlayerNum.SECOND

            }

            override fun onUserMoveStart(grid: Grid, srcEntry: GridEntry) {

            }

            override fun onUserMoveEnd(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry) {
                if (srcEntry == dstEntry) {
                    return
                }

                grid.attemptMove(srcEntry, dstEntry)

                val gameOver = grid.isGameOver
                if (gameOver != null) {
                    handleEndGame(gameOver)
                    return
                }

                if (dstEntry.player == PlayerNum.SECOND) {
                    executeAiMove()
                }
            }
        }
    }

    private fun handleEndGame(end: GameEnd, delay: Long = 1500) {
        gameEnded = true
        Thread().apply {
            Thread.sleep(delay)
            runOnUiThread {
                when (end.winner) {
                    PlayerNum.SECOND -> {
                        gameEndReason.text = getEndGameString(end.reason, true)
                        gameWinner.text = getString(R.string.game_end_positive)
                    }
                    PlayerNum.FIRST -> {
                        gameEndReason.text = getEndGameString(end.reason, false)
                        gameWinner.text = getString(R.string.game_end_negative)
                    }
                    PlayerNum.NOPLAYER, null -> return@runOnUiThread
                }

                TransitionManager.beginDelayedTransition(gameRoot)
                gameWinner.visibility = View.VISIBLE
                gameEndReason.visibility = View.VISIBLE
                gameEndBackground.visibility = View.VISIBLE
            }
        }.start()
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

    override fun onBackPressed() {
        if (gameEnded) {
            super.onBackPressed()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(getString(R.string.game_quit_dialog_title))
            setMessage(getString(R.string.game_quit_dialog_message))

            setPositiveButton(getString(R.string.game_quit_dialog_positive)) { _, _ -> super.onBackPressed() }
            setNegativeButton(getString(R.string.game_quit_dialog_negative)) { dialog, _ -> dialog.dismiss() }
        }

        builder.create().show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putSerializable(GRID_STATE_KEY, checkersGridView.gridData)
            putSerializable(TURN_KEY, checkersGridView.playerTurn)
        }

        super.onSaveInstanceState(outState)
    }
}