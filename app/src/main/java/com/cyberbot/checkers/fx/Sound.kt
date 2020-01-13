package com.cyberbot.checkers.fx

import android.content.Context
import androidx.annotation.RawRes

class Sound {
    companion object {
        @RawRes
        fun getRandomRawForSoundType(type: SoundType): Int {
            return when(type) {
                SoundType.EXPLOSION -> getRandomExplosionSoundRes()
                SoundType.MOVE -> getRandomMoveSoundRes()
                SoundType.AI_THINK -> getRandomAiThinkSoundRes()
            }
        }

        fun playSound(context: Context, type: SoundType) {
            playSoundRes(context, getRandomRawForSoundType(type))
        }
    }
}

enum class SoundType {
    EXPLOSION,
    MOVE,
    AI_THINK
}