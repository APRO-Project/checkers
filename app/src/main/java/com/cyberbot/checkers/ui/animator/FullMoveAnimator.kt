package com.cyberbot.checkers.ui.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.ViewPropertyAnimator
import com.cyberbot.checkers.game.GridEntry
import java.lang.RuntimeException

class FullMoveAnimator(singleCellSize: Float, sequential: Boolean = false) :
    PieceAnimator(singleCellSize, sequential) {

    fun addPiece(
        srcEntry: GridEntry,
        dstEntry: GridEntry,
        srcScale: Float,
        midScale: Float,
        scaleSequential: Boolean = true,
        moveSequential: Boolean = false,
        dstScale: Float = srcScale
    ) {
        val srcX = ((srcEntry.x) + 0.5F) * singleCellSize
        val srcY = ((srcEntry.y) + 0.5F) * singleCellSize

        val dstX = ((dstEntry.x) + 0.5F) * singleCellSize
        val dstY = ((dstEntry.y) + 0.5F) * singleCellSize

        addPiece(
            srcEntry, srcX, srcY, srcScale, midScale, dstX, dstY,
            scaleSequential, moveSequential, dstScale
        )
    }

    fun addPiece(
        entry: GridEntry,
        srcX: Float,
        srcY: Float,
        srcScale: Float,
        midScale: Float,
        dstX: Float,
        dstY: Float,
        scaleSequential: Boolean = true,
        moveSequential: Boolean = false,
        dstScale: Float = srcScale
    ) {
        addPieceInternal(entry, srcX, srcY, srcScale)
        val values = animatedPieces[entry]
            ?: throw RuntimeException("Piece not added to animatedPieces set")
        animators.add(AnimatorSet().apply {
            val moveAnimator = AnimatorSet().apply {
                val animators = arrayListOf<Animator>(
                    ValueAnimator.ofFloat(srcX, dstX).apply {
                        addUpdateListener { animator ->
                            values.x = animator.animatedValue as Float
                            onUpdate(entry, values)
                        }
                    }, ValueAnimator.ofFloat(srcY, dstY).apply {
                        addUpdateListener { animator ->
                            values.y = animator.animatedValue as Float
                            onUpdate(entry, values)
                        }
                    })

                if (moveSequential) {
                    playSequentially(animators)
                } else {
                    playTogether(animators)
                }
            }

            if (scaleSequential) {
                playSequentially(
                    ValueAnimator.ofFloat(srcScale, midScale).apply {
                        addUpdateListener { animator ->
                            values.scale = animator.animatedValue as Float
                            onUpdate(entry, values)
                        }
                    },
                    moveAnimator,
                    ValueAnimator.ofFloat(midScale, dstScale).apply {
                        addUpdateListener { animator ->
                            values.scale = animator.animatedValue as Float
                            onUpdate(entry, values)
                        }
                    }
                )
            } else {
                playTogether(
                    ValueAnimator.ofFloat(srcScale, midScale, dstScale).apply {
                        addUpdateListener { animator ->
                            values.scale = animator.animatedValue as Float
                            onUpdate(entry, values)
                        }
                    },
                    moveAnimator
                )
            }
        })
    }
}