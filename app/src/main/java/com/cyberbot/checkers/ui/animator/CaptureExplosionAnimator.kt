package com.cyberbot.checkers.ui.animator

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnStart
import com.cyberbot.checkers.fx.SoundType
import com.cyberbot.checkers.game.logic.GridEntry
import kotlin.math.roundToInt
import kotlin.random.Random

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
        lowScale: Float = 0.6F
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
        lowScale: Float = 0.6F
    ) {

        val values = addPieceInternal(entry, srcX, srcY, srcScale)

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
                ValueAnimator.ofFloat(//TODO: Make these random
                    0F, -0.005F, 0.008F, -0.0125F, 0.02F,
                    -0.029F, 0.04F, -0.053F, 0.007F, -0.0035F, 0F
                ).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        gridVibrationListener?.invoke(
                            value,
                            if (Random.nextBoolean()) value else -value
                        )
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
            duration = 250
        }

        addAnimator(
            AnimatorSet().apply {
                playSequentially(
                    riseAnimator,
                    hitAnimator,
                    ValueAnimator.ofFloat(lowScale, dstScale).apply {
                        addUpdateListener {
                            values.scale = it.animatedValue as Float
                            onUpdate(entry, values)
                        }

                        duration = 750
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