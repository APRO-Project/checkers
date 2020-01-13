package com.cyberbot.checkers.ui.animator

import android.animation.Animator
import android.animation.AnimatorSet
import com.cyberbot.checkers.game.GridEntry

abstract class PieceAnimator(private val singleCellSize: Float, private var sequential: Boolean) {
    val animatedPieces = HashMap<GridEntry, AnimatedPieceValues>()
    protected val animatorUpdateListeners = ArrayList<AnimatorUpdateListener>()
    protected val animators = ArrayList<Animator>()

    protected fun addPieceInternal(entry: GridEntry, x: Float, y: Float, scale: Float) {
        animatedPieces[entry] = (AnimatedPieceValues(x, y, scale))
    }

    protected fun addPieceInternal(entry: GridEntry, scale: Float = 1F) {
        val cx = (entry.x + 0.5F) * singleCellSize
        val cy = (entry.y + 0.5F) * singleCellSize

        animatedPieces[entry] = (AnimatedPieceValues(cx, cy, scale))
    }

    fun addUpdateListener(action: (GridEntry, AnimatedPieceValues) -> Unit) {
        animatorUpdateListeners.add(object :
            AnimatorUpdateListener {
            override fun onUpdate(entry: GridEntry, values: AnimatedPieceValues) {
                action(entry, values)
            }
        })
    }

    fun addUpdateListener(listener: AnimatorUpdateListener) {
        animatorUpdateListeners.add(listener)
    }

    fun removeAllUpdateListeners() {
        animatorUpdateListeners.clear()
    }

    fun removeUpdateListener(listener: AnimatorUpdateListener) {
        animatorUpdateListeners.remove(listener)
    }

    fun createAnimator(): Animator {
        return AnimatorSet().apply {
            if (sequential) {
                playSequentially(animators)
            } else {
                playTogether(animators)
            }
        }
    }
}

interface AnimatorUpdateListener {
    fun onUpdate(entry: GridEntry, values: AnimatedPieceValues)
}
