package com.cyberbot.checkers.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cyberbot.checkers.R

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        title = getString(R.string.game_options)
    }
}
