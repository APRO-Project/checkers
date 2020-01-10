package com.cyberbot.checkers.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cyberbot.checkers.R
import com.cyberbot.checkers.game.Grid
import com.cyberbot.checkers.preferences.Preferences
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.game_options)

        val prefs = Preferences.fromContext(this)
        settingsGridPreview.allowSecondPlayerMove = false
        settingsGridPreview.allowFirstPlayerMove = false
        settingsGridPreview.gridData = Grid(prefs.gridSize, prefs.playerRows)

        settingsCaptureBackwardSwitch.isChecked = prefs.canCaptureBackwards
        settingsMoveBackwardSwitch.isChecked = prefs.canMoveBackwards
        settingsFlyingKingSwitch.isChecked = prefs.flyingKing

        settingsCaptureBackwardSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.canCaptureBackwards = isChecked
            prefs.save(this)
        }

        settingsMoveBackwardSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.canMoveBackwards = isChecked
            prefs.save(this)
        }

        settingsFlyingKingSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.flyingKing= isChecked
            prefs.save(this)
        }

        settingsGridSizeChipGroup.check(
            when (prefs.gridSize) {
                8 -> R.id.chip8
                10 -> R.id.chip10
                12 -> R.id.chip12
                else -> R.id.chip10
            }
        )

        settingsGridSizeChipGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.chip8 -> {
                    prefs.gridSize = 8
                    prefs.playerRows = 3
                }
                R.id.chip10 -> {

                    prefs.gridSize = 10
                    prefs.playerRows = 4
                }
                R.id.chip12 -> {
                    prefs.gridSize = 12
                    prefs.playerRows = 5
                }
            }

            prefs.save(this)
            settingsGridPreview.gridData = Grid(prefs.gridSize, prefs.playerRows)
        }
    }
}
