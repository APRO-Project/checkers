package com.cyberbot.checkers.ui.animator

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import com.cyberbot.checkers.game.GridEntry
import java.lang.RuntimeException

class ScaleAnimator(singleCellSize: Float, sequential: Boolean = false) :
    PieceAnimator(singleCellSize, sequential) {

    private var targetScale = HashMap<GridEntry, Float>()


    fun addPiece(
        entry: GridEntry,
        srcScale: Float,
        dstScale: Float
    ) {
        targetScale[entry] = dstScale
        addPieceInternal(entry, srcScale)

        val values = animatedPieces[entry]
            ?: throw RuntimeException("Piece not added to animatedPieces set")

        animators.add(ValueAnimator.ofFloat(srcScale, dstScale).apply {
            addUpdateListener { animator ->
                values.scale = animator.animatedValue as Float
                animatorUpdateListeners.forEach {
                    it.onUpdate(entry, values)
                }
            }

            duration = 500L
        })
    }
}