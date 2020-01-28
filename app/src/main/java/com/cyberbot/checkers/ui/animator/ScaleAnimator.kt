package com.cyberbot.checkers.ui.animator

import android.animation.ValueAnimator
import com.cyberbot.checkers.game.logic.GridEntry

class ScaleAnimator(singleCellSize: Float, sequential: Boolean = false) :
    PieceAnimator(singleCellSize, sequential) {

    private var targetScale = HashMap<GridEntry, Float>()

    fun addPiece(
        entry: GridEntry,
        srcScale: Float,
        dstScale: Float
    ) {
        targetScale[entry] = dstScale

        val values = addPieceInternal(entry, srcScale)

        addAnimator(ValueAnimator.ofFloat(srcScale, dstScale).apply {
            addUpdateListener { animator ->
                values.scale = animator.animatedValue as Float
                onUpdate(entry, values)
            }
        })
    }
}