package com.cyberbot.checkers.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyberbot.checkers.R
import com.cyberbot.checkers.game.Grid
import com.cyberbot.checkers.game.PlayerNum
import com.cyberbot.checkers.preferences.Preferences
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.game_options)

        val prefs = Preferences.fromContext(this)
        settingsGridPreview.playerTurn = PlayerNum.NOPLAYER
        settingsGridPreview.gridData = Grid(prefs.gridSize, prefs.playerRows)

        settingsMandatoryCapturesSwitch.isChecked = prefs.mandatoryCapture
        settingsAutoCaptureSwitch.isChecked = prefs.autoCapture
        settingsCaptureBackwardSwitch.isChecked = prefs.canCaptureBackwards
        settingsMoveBackwardSwitch.isChecked = prefs.canMoveBackwards
        settingsFlyingKingSwitch.isChecked = prefs.flyingKing
        settingsCaptureHintsSwitch.isChecked = prefs.captureHints

        settingsMoveBackwardSwitch.isEnabled = prefs.flyingKing
        settingsAutoCaptureSwitch.isEnabled = prefs.mandatoryCapture

        settingsMandatoryCapturesSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.mandatoryCapture = isChecked
            settingsAutoCaptureSwitch.isEnabled = isChecked
            prefs.save(this)
        }

        settingsAutoCaptureSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.autoCapture = isChecked
            prefs.save(this)
        }

        settingsCaptureBackwardSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.canCaptureBackwards = isChecked
            prefs.save(this)
        }

        settingsMoveBackwardSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.canMoveBackwards = isChecked
            prefs.save(this)
        }

        settingsCaptureHintsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.captureHints = isChecked
            prefs.save(this)
        }

        settingsFlyingKingSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.flyingKing = isChecked
            settingsMoveBackwardSwitch.isEnabled = isChecked
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

        settingsDifficultyChipGroup.check(
            when (prefs.aiDepth) {
                1 -> R.id.chipEasy
                2 -> R.id.chipMedium
                4 -> R.id.chipHard
                else -> R.id.chipMedium
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

        settingsDifficultyChipGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.chipEasy -> prefs.aiDepth = 1
                R.id.chipMedium -> prefs.aiDepth = 2
                R.id.chipHard -> prefs.aiDepth = 4
            }

            prefs.save(this)
        }
    }
}
