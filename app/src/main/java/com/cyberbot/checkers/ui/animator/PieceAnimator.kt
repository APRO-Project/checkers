package com.cyberbot.checkers.ui.animator

import android.animation.Animator
import android.animation.AnimatorSet
import com.cyberbot.checkers.game.GridEntry

abstract class PieceAnimator(protected val singleCellSize: Float, private var sequential: Boolean) {
    val animatedPieces = LinkedHashMap<GridEntry, AnimatedPieceValues>()
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

    /**
     *
     * @param action The lambda function to invoke when any of the [AnimatedPieceValues] of the
     * [GridEntry] have been updated
     */
    fun addUpdateListener(action: (GridEntry, AnimatedPieceValues) -> Unit) {
        animatorUpdateListeners.add(object :
            AnimatorUpdateListener {
            override fun onUpdate(entry: GridEntry, values: AnimatedPieceValues) {
                action(entry, values)
            }
        })
    }

    /**
     * Registers a new [AnimatorUpdateListener].
     *
     * @see [AnimatorUpdateListener]
     */
    fun addUpdateListener(listener: AnimatorUpdateListener) {
        animatorUpdateListeners.add(listener)
    }

    fun removeAllUpdateListeners() {
        animatorUpdateListeners.clear()
    }

    fun removeUpdateListener(listener: AnimatorUpdateListener) {
        animatorUpdateListeners.remove(listener)
    }

    /**
     * Creates a new instance of an [AnimatorSet] that contains [Animator]s for all
     * added pieces in either sequential or together mode, depending on [sequential] property
     *
     * @return The animator ready to be played
     */
    fun createAnimator(): AnimatorSet {
        return AnimatorSet().apply {
            if (sequential) {
                playSequentially(animators)
            } else {
                playTogether(animators)
            }
        }
    }

    /**
     * Convenience method for invoking all registered [AnimatorUpdateListener]s
     *
     * @param entry The updated entry
     * @param values Values associated with the [entry]
     */
    protected fun onUpdate(entry: GridEntry, values: AnimatedPieceValues) {
        animatorUpdateListeners.forEach {
            it.onUpdate(entry, values)
        }
    }
}

/**
 * Interface definition for a callback to be invoked when any of the [AnimatedPieceValues]
 * change for a particular [GridEntry].
 */
interface AnimatorUpdateListener {
    /**
     * Called when any of the [AnimatedPieceValues] change for the [entry]
     *
     * @param entry The updated entry
     * @param values Values associated with the [entry]
     */
    fun onUpdate(entry: GridEntry, values: AnimatedPieceValues)
}
