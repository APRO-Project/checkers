package com.cyberbot.checkers.ui.view

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.withTranslation
import com.cyberbot.checkers.R
import com.cyberbot.checkers.fx.Sound
import com.cyberbot.checkers.fx.SoundType
import com.cyberbot.checkers.game.*
import com.cyberbot.checkers.ui.animator.*
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.roundToInt


class CheckersGridView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {

    // <editor-fold defaultstate="collapsed" desc="Colors and paint">
    var gridColorMoveAllowed: Int = 0
        set(value) {
            field = value
            paintGridColorMoveAllowed.color = value
            invalidate()
        }

    var gridColorMoveAllowedHint: Int = 0
        set(value) {
            field = value
            paintGridColorMoveAllowedHint.color = value
            invalidate()
        }

    var gridColorCaptureAllowedHint: Int = 0
        set(value) {
            field = value
            paintGridColorCaptureAllowedHint.color = value
            invalidate()
        }

    var gridColorMoveForbidden: Int = 0
        set(value) {
            field = value
            paintGridColorMoveForbidden.color = value
            invalidate()
        }

    var gridColorMoveSource: Int = 0
        set(value) {
            field = value
            paintGridColorMoveSource.color = value
            invalidate()
        }

    var gridColorLegal: Int = 0
        set(value) {
            field = value
            paintGridColorLegal.color = value
            invalidate()
        }

    var girdColorIllegal: Int = 0
        set(value) {
            field = value
            paintGridColorIllegal.color = value
            invalidate()
        }

    var playerColor1: Int = 0
        set(value) {
            field = value
            paintPlayerColor1.color = value
            invalidate()
        }

    var playerColor2: Int = 0
        set(value) {
            field = value
            paintPlayerColor2.color = value
            invalidate()
        }

    var playerOutlineColor1: Int = 0
        set(value) {
            field = value
            paintPlayerOutlineColor1.color = value
            invalidate()
        }

    var playerOutlineColor2: Int = 0
        set(value) {
            field = value
            paintPlayerOutlineColor2.color = value
            invalidate()
        }

    private val paintGridColorMoveAllowed = Paint(0).apply {
        color = gridColorMoveAllowed
        style = Paint.Style.FILL
    }

    private val paintGridColorMoveAllowedHint = Paint(0).apply {
        color = gridColorMoveAllowedHint
        style = Paint.Style.FILL
    }

    private val paintGridColorCaptureAllowedHint = Paint(0).apply {
        color = gridColorCaptureAllowedHint
        style = Paint.Style.FILL
    }

    private val paintGridColorMoveForbidden = Paint(0).apply {
        color = gridColorMoveForbidden
        style = Paint.Style.FILL
    }

    private val paintGridColorMoveSource = Paint(0).apply {
        color = gridColorMoveSource
        style = Paint.Style.FILL
    }

    private val paintGridColorLegal = Paint(0).apply {
        color = gridColorLegal
        style = Paint.Style.FILL
    }

    private val paintGridColorIllegal = Paint(0).apply {
        color = girdColorIllegal
        style = Paint.Style.FILL
    }

    private val paintPlayerColor1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerColor1
        style = Paint.Style.FILL
    }

    private val paintPlayerColor2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerColor2
        style = Paint.Style.FILL
    }


    private val paintPlayerOutlineColor1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerOutlineColor1
        style = Paint.Style.FILL
    }

    private val paintPlayerOutlineColor2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerOutlineColor2
        style = Paint.Style.FILL
    }
    //</editor-fold>

    private var viewMeasureType: Int = 0
    private var viewWidth: Int = 0
    private var singleCellSize: Float = 0F
    private var playerRadius: Float = 0F
    private var playerRadiusOutline: Float = 0F
    private var playerRadiusIcon: Float = 0F
    private var userInteractionEnabled = true

    var soundFxEnabled = true
    var playerTurn = PlayerNum.NOPLAYER

    var moveAttemptListener: MoveAttemptListener? = null

    var riseAnimationDuration = 500L
    var artificialAnimationDuration = 100L
    var returnAnimationDuration = 500L
    var playerScaleMoving: Float = 1.35F

    /**
     * In relation to the cell size
     */
    var playerSize: Float = 0.65F
        set(value) {
            field = value
            invalidate()
        }

    /**
     * In relation to player size
     */
    var playerOutlineSize: Float = 1.175F
        set(value) {
            field = value
            invalidate()
        }

    /**
     * In relation to the player size
     */
    var playerIconSize: Float = 0.75F
        set(value) {
            field = value
            invalidate()
        }

    var gridData = Grid(8, 3)
        set(value) {
            field = value

            updateDimensions()
            invalidate()
        }

    private var userInteracting = false
    private var movingEntry: GridEntry? = null
    private var moveOffsetX = 0F
    private var moveOffsetY = 0F
    private var moveX = 0F
    private var moveY = 0F

    private var canvasOffsetX = 0F
    private var canvasOffsetY = 0F

    private var currentPieceAnimator: PieceAnimator? = null
    private var currentAnimator: Animator? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CheckersGridView,
            0, 0
        ).apply {
            try {
                gridColorMoveAllowed =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_allowed,
                        context.getColor(R.color.game_color_grid_move_allowed)
                    )
                gridColorMoveAllowedHint =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_allowed_hint,
                        context.getColor(R.color.game_color_grid_move_allowed_hint)
                    )
                gridColorCaptureAllowedHint =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_capture_allowed_hint,
                        context.getColor(R.color.game_color_grid_capture_allowed_hint)
                    )
                gridColorMoveForbidden =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_forbidden,
                        context.getColor(R.color.game_color_grid_move_forbidden)
                    )
                gridColorMoveSource =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_source,
                        context.getColor(R.color.game_color_grid_move_source)
                    )
                gridColorLegal =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_legal,
                        context.getColor(R.color.game_color_grid_default_legal)
                    )
                girdColorIllegal =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_illegal,
                        context.getColor(R.color.game_color_grid_default_illegal)
                    )
                playerColor1 = getColor(
                    R.styleable.CheckersGridView_player_color1,
                    context.getColor(R.color.game_color_player1)
                )
                playerColor2 = getColor(
                    R.styleable.CheckersGridView_player_color2,
                    context.getColor(R.color.game_color_player2)
                )
                playerOutlineColor1 =
                    getColor(
                        R.styleable.CheckersGridView_player_outline_color1,
                        context.getColor(R.color.game_color_player_outline1)
                    )
                playerOutlineColor2 =
                    getColor(
                        R.styleable.CheckersGridView_player_outline_color1,
                        context.getColor(R.color.game_color_player_outline2)
                    )
                viewMeasureType =
                    getInteger(R.styleable.CheckersGridView_view_size, 0)
            } finally {
                recycle()
            }
        }
    }

    private fun drawGridEntry(
        canvas: Canvas, entry: GridEntry,
        paint: Paint = if (entry.legal()) paintGridColorLegal else paintGridColorIllegal
    ) {
        val left = entry.x * singleCellSize
        val top = entry.y * singleCellSize

        canvas.drawRect(
            left,
            top,
            left + singleCellSize,
            top + singleCellSize,
            paint
        )
    }

    private fun drawPlayer(
        canvas: Canvas,
        entry: GridEntry,
        cx: Float,
        cy: Float,
        scale: Float = 1F
    ) {
        canvas.apply {
            when (entry.player) {
                PlayerNum.FIRST -> {
                    drawCircle(cx, cy, playerRadiusOutline * scale, paintPlayerOutlineColor1)
                    drawCircle(cx, cy, playerRadius * scale, paintPlayerColor1)
                }
                PlayerNum.SECOND -> {
                    drawCircle(cx, cy, playerRadiusOutline * scale, paintPlayerOutlineColor2)
                    drawCircle(cx, cy, playerRadius * scale, paintPlayerColor2)
                }
                PlayerNum.NOPLAYER, null -> {
                    // Do not draw
                }
            }

            if (entry.pieceType == PieceType.KING) {
                val d = resources.getDrawable(R.drawable.ic_king, null)
                when (entry.player) {
                    PlayerNum.NOPLAYER, null -> return
                    PlayerNum.FIRST -> d.setTint(playerOutlineColor1)
                    PlayerNum.SECOND -> d.setTint(playerOutlineColor2)
                }

                d.setBounds(
                    (cx - playerRadiusIcon * scale).roundToInt(),
                    (cy - playerRadiusIcon * scale).roundToInt(),
                    (cx + playerRadiusIcon * scale).roundToInt(),
                    (cy + playerRadiusIcon * scale).roundToInt()
                )

                d.draw(canvas)
            }
        }
    }

    fun attemptMove(srcEntry: GridEntry, dstEntry: GridEntry): Boolean {
        if (!gridData.destinationAllowed(srcEntry, dstEntry)) {
            return false
        }

        val destination = gridData.getDestination(srcEntry, dstEntry)

        if (destination.isCapture) {
            currentPieceAnimator = CaptureExplosionAnimator(singleCellSize).apply {
                destination.capturedPieces.forEach { addTargetPiece(it) }
                setDestroyerPiece(
                    srcEntry,
                    playerScaleMoving,
                    destination.destinationEntry
                )

                pieceTypeRemovedListener = {
                    gridData.removeGridEntry(it)
                }

                gridVibrationListener = { x, y ->
                    canvasOffsetX = x * viewWidth
                    canvasOffsetY = y * viewWidth
                }

                soundEffectListener = {
                    Sound.playSound(context, it)
                }
                soundEffectListener = {
                    Sound.playSound(context, it)
                }

                addUpdateListener { _, _ ->
                    invalidate()
                }

                currentAnimator = createAnimator().apply {
                    doOnEnd {
                        currentPieceAnimator = null
                        currentAnimator = null

                        moveAttemptListener?.onForcedMoveEnd(gridData, srcEntry, dstEntry)

                        canvasOffsetX = 0F
                        canvasOffsetY = 0F
                        invalidate()
                    }

                    start()
                }
            }
        } else {
            currentPieceAnimator = FullMoveAnimator(singleCellSize).apply {
                addPiece(srcEntry, dstEntry, 1F, playerScaleMoving)
                addUpdateListener { _, v ->
                    invalidate()
                }

                currentAnimator = createAnimator().apply {
                    doOnStart {
                        if (soundFxEnabled) Sound.playSound(context, SoundType.MOVE)
                        moveAttemptListener?.onForcedMoveStart(gridData, srcEntry, dstEntry)
                    }

                    doOnEnd {
                        currentPieceAnimator = null
                        currentAnimator = null

                        moveAttemptListener?.onForcedMoveEnd(gridData, srcEntry, dstEntry)
                    }

                    duration = artificialAnimationDuration
                    start()
                }
            }
        }
        return true
    }

    private fun playerMoveAllowed(player: PlayerNum): Boolean {
        return player != PlayerNum.NOPLAYER && player == playerTurn
    }

    private fun updateDimensions() {
        singleCellSize = viewWidth.toFloat() / gridData.size
        playerRadius = singleCellSize * playerSize * 0.5F
        playerRadiusOutline = singleCellSize * playerSize * playerOutlineSize * 0.5F
        playerRadiusIcon = singleCellSize * playerSize * playerIconSize * 0.5F
    }

    private fun handleMove(srcEntry: GridEntry, dstEntry: GridEntry) {
        val destination = gridData.getDestination(srcEntry, dstEntry)
            ?: throw java.lang.RuntimeException("Move here is not allowed")

        movingEntry = null

        if (destination.isCapture) {
            handleMoveCapture(srcEntry, destination)
        } else {
            handleMoveNormal(srcEntry, destination)
        }

        moveY = 0F
        moveX = 0F
    }

    private fun handleMoveNormal(srcEntry: GridEntry, destination: Destination) {
        val dstEntry = destination.destinationEntry

        currentPieceAnimator = MoveScaleAnimator(singleCellSize).apply {
            addPiece(srcEntry, dstEntry, moveX, moveY, playerScaleMoving)
            addUpdateListener { _, _ ->
                invalidate()
            }

            currentAnimator = createAnimator().apply {
                doOnEnd {
                    currentAnimator = null
                    currentPieceAnimator = null
                    userInteractionEnabled = true

                    moveAttemptListener?.onUserMoveEnd(gridData, srcEntry, dstEntry)
                    invalidate()
                }

                duration = returnAnimationDuration
                start()
            }
        }
    }

    private fun handleMoveCapture(srcEntry: GridEntry, destination: Destination) {
        currentPieceAnimator = CaptureExplosionAnimator(singleCellSize).apply {
            destination.capturedPieces.forEach { addTargetPiece(it) }
            setDestroyerPiece(
                srcEntry,
                moveX,
                moveY,
                playerScaleMoving,
                destination.destinationEntry
            )

            pieceTypeRemovedListener = {
                gridData.removeGridEntry(it)
            }

            gridVibrationListener = { x, y ->
                canvasOffsetX = x * viewWidth
                canvasOffsetY = y * viewWidth
            }

            soundEffectListener = {
                Sound.playSound(context, it)
            }

            addUpdateListener { _, _ ->
                invalidate()
            }

            currentAnimator = createAnimator().apply {
                doOnEnd {
                    currentAnimator = null
                    currentPieceAnimator = null
                    userInteractionEnabled = true

                    moveAttemptListener?.onUserMoveEnd(
                        gridData,
                        srcEntry,
                        destination.destinationEntry
                    )

                    canvasOffsetX = 0F
                    canvasOffsetY = 0F
                    invalidate()
                }

                start()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withTranslation(canvasOffsetX, canvasOffsetY) {
            gridData.forEach {
                drawGridEntry(this, it)

                currentPieceAnimator.let { animator ->
                    if (animator == null || !animator.animatedPieces.containsKey(it)) {
                        val cx = (it.x + 0.5F) * singleCellSize
                        val cy = (it.y + 0.5F) * singleCellSize
                        drawPlayer(this, it, cx, cy)
                    }
                }
            }

            movingEntry?.let { srcEntry ->
                val x = (moveX / singleCellSize).toInt()
                val y = (moveY / singleCellSize).toInt()
                val dstEntry = gridData.getEntryByCoords(x, y)

                if (userInteracting) {
                    val allowedEntries = gridData.getMovableEntries(playerTurn)
                    allowedEntries[movingEntry]?.forEach {
                        drawGridEntry(
                            this, it.destinationEntry,
                            if (it.isCapture) paintGridColorCaptureAllowedHint
                            else paintGridColorMoveAllowedHint
                        )
                    }

                    if (dstEntry != movingEntry) {
                        drawGridEntry(
                            this, dstEntry,
                            if (gridData.destinationAllowed(srcEntry, dstEntry))
                                paintGridColorMoveAllowed else paintGridColorMoveForbidden
                        )
                    }

                    drawGridEntry(this, srcEntry, paintGridColorMoveSource)

                    val cx = (dstEntry.x + 0.5F) * singleCellSize
                    val cy = (dstEntry.y + 0.5F) * singleCellSize
                    if (dstEntry != srcEntry) {
                        drawPlayer(this, dstEntry, cx, cy)
                    }
                }
            }

            currentPieceAnimator.let { animator ->
                if (animator !== null) {
                    animator.animatedPieces.forEach { (e, v) ->
                        if (e == movingEntry) {
                            drawPlayer(this, e, moveX, moveY, v.scale)
                        } else {
                            drawPlayer(this, e, v.x, v.y, v.scale)
                        }
                    }
                } else {
                    movingEntry?.let {
                        drawPlayer(this, it, moveX, moveY, playerScaleMoving)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        if (!userInteractionEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                userInteracting = true
                currentAnimator?.let {
                    it.cancel()
                    currentPieceAnimator = null
                    currentAnimator = null
                }

                val x = (event.x / singleCellSize).toInt()
                val y = (event.y / singleCellSize).toInt()

                val entry = gridData.getEntryByCoords(x, y)
                if (!playerMoveAllowed(entry.player)) {
                    return true
                }

                moveAttemptListener?.onUserMoveStart(gridData, entry)

                val cx = (entry.x + 0.5F) * singleCellSize
                val cy = (entry.y + 0.5F) * singleCellSize

                moveOffsetX = event.x - cx
                moveOffsetY = event.y - cy
                moveX = cx
                moveY = cy

                if (movingEntry == null) {
                    currentPieceAnimator = ScaleAnimator(singleCellSize).apply {
                        addPiece(entry, 1F, playerScaleMoving)
                        addUpdateListener { _, _ ->
                            invalidate()
                        }

                        currentAnimator = createAnimator().apply {
                            doOnEnd {
                                currentAnimator = null
                            }

                            duration = riseAnimationDuration
                            start()
                        }
                    }
                }

                movingEntry = entry
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                userInteracting = true
                moveX = max(min(event.x - moveOffsetX, viewWidth.toFloat() - 1), 0F)
                moveY = max(min(event.y - moveOffsetY, viewWidth.toFloat() - 1), 0F)

                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                val x = (moveX / singleCellSize).toInt()
                val y = (moveY / singleCellSize).toInt()
                val dstEntry = gridData.getEntryByCoords(x, y)

                moveOffsetX = 0F
                moveOffsetY = 0F

                userInteracting = false

                if (movingEntry == null) {
                    return false
                }

                userInteractionEnabled = false

                movingEntry?.let { srcEntry ->
                    val entry = if (gridData.destinationAllowed(srcEntry, dstEntry)) {
                        dstEntry
                    } else {
                        srcEntry
                    }

                    handleMove(srcEntry, entry)

                }
                return true
            }
        }

        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w == 0 || h == 0) {
            return
        }

        viewWidth = w
        updateDimensions()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = when (viewMeasureType) {
            0 -> {
                if (width > height) height else width
            }
            1 -> width
            2 -> height
            else -> throw RuntimeException("Invalid view_size attribute")
        }
        setMeasuredDimension(size, size)
    }
}

interface MoveAttemptListener {
    fun onUserMoveStart(grid: Grid, srcEntry: GridEntry)
    fun onUserMoveEnd(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry)

    fun onForcedMoveStart(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry)
    fun onForcedMoveEnd(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry)
}
