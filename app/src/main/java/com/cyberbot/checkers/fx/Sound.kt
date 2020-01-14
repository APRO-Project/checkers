package com.cyberbot.checkers.fx

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.cyberbot.checkers.R

class Sound {
    companion object {
        var mediaPlayer : MediaPlayer? = null

        @RawRes
        fun getRandomRawForSoundType(type: SoundType): Int {
            return when(type) {
                SoundType.EXPLOSION -> getRandomExplosionSoundRes()
                SoundType.MOVE -> getRandomMoveSoundRes()
                SoundType.AI_THINK -> getRandomAiThinkSoundRes()
                SoundType.EXPLOSION_LONG -> R.raw.explode_long1
            }
        }

        fun playSound(context: Context, type: SoundType) {
            mediaPlayer?.stop()
            mediaPlayer?.release()

            mediaPlayer = MediaPlayer.create(context, getRandomRawForSoundType(type))
            mediaPlayer?.start()
        }
    }
}

enum class SoundType {
    EXPLOSION,
    MOVE,
    AI_THINK,
    EXPLOSION_LONG
}