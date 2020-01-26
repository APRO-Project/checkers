package com.cyberbot.checkers.ui.animator

import android.animation.Animator
import android.animation.AnimatorSet
import com.cyberbot.checkers.game.GridEntry

/**
 *
 *
 * @property singleCellSize Size of a single grid cell in pixels.
 * Required to correctly compute destination coordinates.
 *
 * @property sequential Whether the generated [Animator] plays sequentially or together.
 *
 * @see AnimatedPieceValues
 */
abstract class PieceAnimator(protected val singleCellSize: Float, private var sequential: Boolean) :
    Iterable<Map.Entry<GridEntry, AnimatedPieceValues>> {

    /**
     * HashMap storing all animated pieces (order matters when [sequential] is
     * set to <code>true</code>).
     */
    private val animatedPieces = LinkedHashMap<GridEntry, AnimatedPieceValues>()
    private val animatorUpdateListeners = ArrayList<AnimatorUpdateListener>()
    private val animators = ArrayList<Animator>()

    /**
     * Returns the [AnimatedPieceValues] for a [GridEntry]
     *
     * @param entry The entry associated with the values
     * @return AnimatedPieceValues if the [entry] is being animated null otherwise
     *
     * @see AnimatedPieceValues
     */
    fun getValuesForEntry(entry: GridEntry): AnimatedPieceValues? {
        return animatedPieces[entry]
    }

    /**
     * Returns true if this animator animates the
     * specified entry.
     *
     * @param   entry   The entry whose presence in this map is to be tested
     * @return true if this animator contains a mapping for the specified
     * entry.
     */
    fun containsEntry(entry: GridEntry): Boolean {
        return animatedPieces.containsKey(entry)
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

    /**
     * Removes all [AnimatorUpdateListener]s.
     *
     * @see [AnimatorUpdateListener]
     */
    fun removeAllUpdateListeners() {
        animatorUpdateListeners.clear()
    }

    /**
     * Removes a specific [AnimatorUpdateListener].
     *
     * @see [AnimatorUpdateListener]
     */
    fun removeUpdateListener(listener: AnimatorUpdateListener) {
        animatorUpdateListeners.remove(listener)
    }

    /**
     * Creates a new instance of an [AnimatorSet] that contains [Animator]s for all
     * added pieces in either sequential or together mode, depending on [sequential] property.
     *
     * @return The animator ready to be played.
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
     * Add a new entry to [animatedPieces]. It has to be called before creating animators
     * in order to properly initialize entry's [AnimatedPieceValues].
     *
     * @param entry The grid entry to associate the values with.
     * @param x The horizontal component of the starting position (in pixels).
     * @param y The vertical component of the starting position (in pixels).
     * @param scale The starting scale.
     *
     * @return Initialized [AnimatedPieceValues].
     */
    protected fun addPieceInternal(
        entry: GridEntry,
        x: Float,
        y: Float,
        scale: Float
    ): AnimatedPieceValues {
        val values = AnimatedPieceValues(x, y, scale)
        animatedPieces[entry] = values
        return values
    }

    /**
     * Add a new entry to [animatedPieces]. It has to be called before creating animators
     * in order to properly initialize entry's [AnimatedPieceValues].
     * The starting positions are calculated using [singleCellSize] property value.
     *
     * @param entry The grid entry to associate the values with.
     * @param scale The starting scale
     *
     * @return Initialized [AnimatedPieceValues]
     */
    protected fun addPieceInternal(entry: GridEntry, scale: Float = 1F): AnimatedPieceValues {
        val cx = (entry.x + 0.5F) * singleCellSize
        val cy = (entry.y + 0.5F) * singleCellSize

        return addPieceInternal(entry, cx, cy, scale)
    }

    /**
     * Adds a new [Animator] to the set.
     *
     * @param animator The animator to add to the list.
     */
    protected fun addAnimator(animator: Animator) {
        addAnimator(animator)
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

    /**
     * Returns the iterator for animated entries and it's values.
     *
     * @return The iterator for animated entries and it's values.
     */
    override fun iterator(): Iterator<Map.Entry<GridEntry, AnimatedPieceValues>> {
        return animatedPieces.iterator()
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
