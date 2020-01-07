package com.cyberbot.checkers.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyberbot.checkers.R
import com.cyberbot.checkers.game.GridEntry
import com.cyberbot.checkers.game.PlayerNum
import com.cyberbot.checkers.ui.view.MoveUpdateListener
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        move_player2.text = getString(R.string.game_player_turn_info)

        val gridData = checkersGridView.gridData

        checkersGridView.moveUpdateListener = object : MoveUpdateListener {
            override fun onMoveStart(srcEntry: GridEntry, dstEntry: GridEntry) {
                if(srcEntry.player == PlayerNum.FIRST) {
                    checkersGridView.userInteractionEnabled = false
                    move_player2.text = "Busy"
                }
            }

            override fun onMoveEnd(srcEntry: GridEntry, dstEntry: GridEntry) {
                if(srcEntry.player == PlayerNum.FIRST) {
                    checkersGridView.userInteractionEnabled = true
                    move_player2.text = getString(R.string.game_player_turn_info)
                }
                if (srcEntry == dstEntry) return

                gridData.attemptMove(srcEntry, dstEntry)
                if (dstEntry.player == PlayerNum.SECOND) {
                    val src: GridEntry = gridData.first {
                        it.player == PlayerNum.FIRST
                    }

                    val dst: GridEntry = gridData.first {
                        it != src && gridData.moveAllowed(src, it)
                    }

                    checkersGridView.attemptMove(src, dst)
                }
            }
        }
    }
}