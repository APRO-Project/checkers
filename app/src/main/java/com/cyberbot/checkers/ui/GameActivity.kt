package com.cyberbot.checkers.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cyberbot.checkers.R
import com.cyberbot.checkers.game.Grid
import com.cyberbot.checkers.game.GridEntry
import com.cyberbot.checkers.game.GridUpdateListener
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        move_player1.text = getString(R.string.game_player_turn_info)

        val gridData = checkersGridView.gridData
        gridData.gridUpdateListener = object : GridUpdateListener {
            override fun move(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry) {
                grid.allowFirstPlayerMove = grid.allowSecondPlayerMove
                grid.allowSecondPlayerMove = !grid.allowSecondPlayerMove

                if (grid.allowFirstPlayerMove) {
                    move_player1.text = getString(R.string.game_player_turn_info)
                    move_player2.text = ""
                } else {
                    move_player1.text = ""
                    move_player2.text = getString(R.string.game_player_turn_info)
                }
            }
        }
    }
}
