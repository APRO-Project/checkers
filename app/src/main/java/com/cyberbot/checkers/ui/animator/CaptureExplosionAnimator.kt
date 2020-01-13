package com.cyberbot.checkers.ui.animator

import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Interpolator
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.cyberbot.checkers.fx.SoundType
import com.cyberbot.checkers.game.GridEntry
import java.lang.RuntimeException
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class CaptureExplosionAnimator(singleCellSize: Float) :
    PieceAnimator(singleCellSize, sequential = false) {

    var riseAnimationDuration = 1000L
    var fallAnimationDuration = 250L

    var pieceTypeRemovedListener: ((GridEntry) -> Unit)? = null
    var gridVibrationListener: ((Float, Float) -> Unit)? = null
    var soundEffectListener: ((SoundType) -> Unit)? = null

    var targetEntries = ArrayList<GridEntry>()

    fun setDestroyerPiece(
        entry: GridEntry,
        srcScale: Float,
        dstEntry: GridEntry,
        dstScale: Float = 1F,
        topScale: Float = 3F,
        lowScale: Float = 0.8F
    ) {
        val cx = (entry.x + 0.5F) * singleCellSize
        val cy = (entry.y + 0.5F) * singleCellSize

        setDestroyerPiece(entry, cx, cy, srcScale, dstEntry, dstScale, topScale, lowScale)
    }

    fun setDestroyerPiece(
        entry: GridEntry,
        srcX: Float,
        srcY: Float,
        srcScale: Float,
        dstEntry: GridEntry,
        dstScale: Float = 1F,
        topScale: Float = 3F,
        lowScale: Float = 0.8F
    ) {
        addPieceInternal(entry, srcX, srcY, srcScale)
        val values =
            animatedPieces[entry] ?: throw RuntimeException("Piece not added to animatedPieces set")

        val dstX = (dstEntry.x + 0.5F) * singleCellSize
        val dstY = (dstEntry.y + 0.5F) * singleCellSize

        val hitTarget = calculateTarget()

        val riseAnimator = AnimatorSet().apply {
            playTogether(
                ValueAnimator.ofFloat(srcScale, topScale).apply {
                    addUpdateListener {
                        values.scale = it.animatedValue as Float
                        onUpdate(entry, values)
                    }
                },
                ValueAnimator.ofFloat(srcX, hitTarget.first).apply {
                    addUpdateListener {
                        values.x = it.animatedValue as Float
                        onUpdate(entry, values)
                    }
                },
                ValueAnimator.ofFloat(srcY, hitTarget.second).apply {
                    addUpdateListener {
                        values.y = it.animatedValue as Float
                        onUpdate(entry, values)
                    }
                }
            )

            doOnStart {
                soundEffectListener?.invoke(SoundType.EXPLOSION_LONG)
            }

            duration = riseAnimationDuration
        }

        val hitAnimator = AnimatorSet().apply {
            var notifyRemove = true
            playTogether(
                ValueAnimator.ofFloat(topScale, lowScale).apply {
                    addUpdateListener {
                        val scale = it.animatedValue as Float
                        if (scale < 1F && notifyRemove) {
                            notifyRemove = false
                            targetEntries.forEach { e -> pieceTypeRemovedListener?.invoke(e) }
                        }

                        values.scale = scale
                        onUpdate(entry, values)
                    }

                    interpolator = PathInterpolator(1.000F, 0.000F, 0.675F, 0.190F)
                },
                ValueAnimator.ofFloat(0F, 8F, -10F, 15F, -20F, 25F, -30F, 10F, -5F, 0F).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        gridVibrationListener?.invoke(value, -value)
                        onUpdate(entry, values)
                    }
                }
            )

            duration = fallAnimationDuration
        }

        val moveDstAnimator = AnimatorSet().apply {
            playTogether(
                ValueAnimator.ofFloat(hitTarget.first, dstX).apply {
                    addUpdateListener {
                        values.x = it.animatedValue as Float
                        onUpdate(entry, values)
                    }
                },
                ValueAnimator.ofFloat(hitTarget.second, dstY).apply {
                    addUpdateListener {
                        values.y = it.animatedValue as Float
                        onUpdate(entry, values)
                    }
                })
            duration = 500
        }

        animators.add(
            AnimatorSet().apply {
                playSequentially(
                    riseAnimator,
                    hitAnimator,
                    ValueAnimator.ofFloat(lowScale, dstScale).apply {
                        addUpdateListener {
                            values.scale = it.animatedValue as Float
                            onUpdate(entry, values)
                        }

                        duration = 500
                    },
                    moveDstAnimator
                )
            }
        )
    }

    fun addTargetPiece(entry: GridEntry) {
        targetEntries.add(entry)
        addPieceInternal(entry)
    }

    private fun calculateTarget(): Pair<Float, Float> {
        val avgX = (targetEntries.map { it.x }.average().roundToInt() + 0.5) * singleCellSize
        val avgY = (targetEntries.map { it.y }.average().roundToInt() + 0.5) * singleCellSize

        return Pair(avgX.toFloat(), avgY.toFloat())
    }
}