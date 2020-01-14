package com.cyberbot.checkers.ui.animator

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import com.cyberbot.checkers.game.GridEntry

class MoveScaleAnimator(singleCellSize: Float, sequential: Boolean = false) :
    PieceAnimator(singleCellSize, sequential) {

    fun addPiece(
        entry: GridEntry,
        srcX: Float,
        srcY: Float,
        srcScale: Float,
        dstX: Float,
        dstY: Float,
        dstScale: Float
    ) {
        addPieceInternal(entry, srcX, srcY, srcScale)
        val values = animatedPieces[entry]
            ?: throw RuntimeException("Piece not added to animatedPieces set")
        animators.add(AnimatorSet().apply {
            playTogether(
                ValueAnimator.ofFloat(srcScale, dstScale).apply {
                    addUpdateListener { animator ->
                        values.scale = animator.animatedValue as Float
                        onUpdate(entry, values)
                    }
                },
                ValueAnimator.ofFloat(srcX, dstX).apply {
                    addUpdateListener { animator ->
                        values.x = animator.animatedValue as Float
                        onUpdate(entry, values)
                    }
                },
                ValueAnimator.ofFloat(srcY, dstY).apply {
                    addUpdateListener { animator ->
                        values.y = animator.animatedValue as Float
                        onUpdate(entry, values)
                    }
                }
            )
        })
    }

    fun addPiece(
        entry: GridEntry,
        srcX: Float,
        srcY: Float,
        dstEntry: GridEntry,
        srcScale: Float,
        dstScale: Float = 1F
    ) {

        val dstX = (dstEntry.x + 0.5F) * singleCellSize
        val dstY = (dstEntry.y + 0.5F) * singleCellSize

        addPiece(entry, srcX, srcY, srcScale, dstX, dstY, dstScale)
    }

    fun addPiece(
        srcEntry: GridEntry,
        dstEntry: GridEntry,
        singleCellSize: Float,
        srcScale: Float = 1F,
        dstScale: Float = 1F
    ) {
        val srcX = (srcEntry.x + 0.5F) * singleCellSize
        val srcY = (srcEntry.y + 0.5F) * singleCellSize

        val dstX = (dstEntry.x + 0.5F) * singleCellSize
        val dstY = (dstEntry.y + 0.5F) * singleCellSize

        addPiece(srcEntry, srcX, srcY, srcScale, dstX, dstY, dstScale)
    }
}
