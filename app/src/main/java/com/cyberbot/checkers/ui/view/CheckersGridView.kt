package com.cyberbot.checkers.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.cyberbot.checkers.R
import com.cyberbot.checkers.game.Grid
import com.cyberbot.checkers.game.GridEntry
import com.cyberbot.checkers.game.PlayerNum


class CheckersGridView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {
    companion object {
        val COLOR_DEFAULT_GRID = Color.rgb(0.5F, 0.5F, 0.5F)
        val COLOR_DEFAULT_DROP_ALLOWED = Color.rgb(118, 230, 62)
        val COLOR_DEFAULT_DROP_FORBIDDEN = Color.rgb(230, 62, 62)
    }

    // <editor-fold defaultstate="collapsed" desc="Colors and paint">
    var gridColorMoveAllowed: Int = 0
        set(value) {
            field = value
            paintGridColorMoveAllowed.color = value
            invalidate()
        }
    var gridColorMoveForbidden: Int = 0
        set(value) {
            field = value
            paintGridColorMoveForbidden.color = value
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

    private val paintGridColorMoveForbidden = Paint(0).apply {
        color = gridColorMoveForbidden
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

    private var singleCellSize: Float = 0F
    private var playerRadius: Float = 0F
    private var playerRadiusOutline: Float = 0F

    var riseAnimationDuration = 500L
    var returnAnimationDuration = 500L
    var playerScaleMoving: Float = 1.35F
    var playerSize: Float = 0.6F
        set(value) {
            field = value
            invalidate()
        }
    var playerOutlineSize: Float = 0.7F
        set(value) {
            field = value
            invalidate()
        }

    var gridData = Grid(8, 3)
        set(value) {
            field = value
            invalidate()
        }

    private var userInteracting = false
    private var playerScaleCurrent = 1F
    private var movingEntry: GridEntry? = null
    private var moveOffsetX: Float = 0F
    private var moveOffsetY: Float = 0F
    private var moveX = 0F
    private var moveY = 0F
    private var returnAnimatorSet: AnimatorSet? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CheckersGridView,
            0, 0
        ).apply {
            try {
                gridColorMoveAllowed =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_legal,
                        COLOR_DEFAULT_DROP_ALLOWED
                    )
                gridColorMoveForbidden =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_legal,
                        COLOR_DEFAULT_DROP_FORBIDDEN
                    )
                gridColorLegal =
                    getColor(R.styleable.CheckersGridView_grid_color_legal, COLOR_DEFAULT_GRID)
                girdColorIllegal =
                    getColor(R.styleable.CheckersGridView_grid_color_illegal, Color.WHITE)
                playerColor1 = getColor(R.styleable.CheckersGridView_player_color1, Color.WHITE)
                playerColor2 = getColor(R.styleable.CheckersGridView_player_color2, Color.BLACK)
                playerOutlineColor1 =
                    getColor(R.styleable.CheckersGridView_player_outline_color1, Color.BLACK)
                playerOutlineColor2 =
                    getColor(R.styleable.CheckersGridView_player_outline_color1, Color.WHITE)
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
                PlayerNum.NOPLAYER -> {
                    // Do not draw
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            gridData.forEach {
                drawGridEntry(this, it)

                if (it != movingEntry) {
                    val cx = (it.x + 0.5F) * singleCellSize
                    val cy = (it.y + 0.5F) * singleCellSize
                    drawPlayer(this, it, cx, cy)
                }
            }

            movingEntry?.let { entry ->
                val x = (moveX / singleCellSize).toInt()
                val y = (moveY / singleCellSize).toInt()
                val dstEntry = gridData.getEntryByCoords(x, y)

                if (userInteracting) {
                    drawGridEntry(
                        this, dstEntry,
                        if (gridData.moveAllowed(entry, dstEntry))
                            paintGridColorMoveAllowed else paintGridColorMoveForbidden
                    )
                }

                drawPlayer(this, entry, moveX, moveY, playerScaleCurrent)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                userInteracting = true
                returnAnimatorSet?.cancel()
                returnAnimatorSet = null

                val x = (event.x / singleCellSize).toInt()
                val y = (event.y / singleCellSize).toInt()

                val entry = gridData.getEntryByCoords(x, y)
                if (!gridData.playerMoveAllowed(entry.player)) {
                    return true
                }

                val cx = (entry.x + 0.5F) * singleCellSize
                val cy = (entry.y + 0.5F) * singleCellSize

                moveOffsetX = event.x - cx
                moveOffsetY = event.y - cy
                moveX = cx
                moveY = cy

                if (movingEntry == null) {
                    ValueAnimator.ofFloat(1F, playerScaleMoving).apply {
                        addUpdateListener {
                            playerScaleCurrent = it.animatedValue as Float
                        }

                        duration = riseAnimationDuration
                        start()
                    }
                }

                movingEntry = entry

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                userInteracting = true
                moveX = event.x - moveOffsetX
                moveY = event.y - moveOffsetY

                invalidate()

                return true
            }
            MotionEvent.ACTION_UP -> {
                userInteracting = false
                val x = (moveX / singleCellSize).toInt()
                val y = (moveY / singleCellSize).toInt()
                val dstEntry = gridData.getEntryByCoords(x, y)

                moveOffsetX = 0F
                moveOffsetY = 0F

                movingEntry?.let {
                    if(gridData.attemptMove(it, dstEntry)) {
                        movingEntry = dstEntry
                    }
                }

                movingEntry?.let {entry ->
                    val dstX = ((entry.x) + 0.5F) * singleCellSize
                    val dstY = ((entry.y) + 0.5F) * singleCellSize

                    val srcX = moveX
                    val srcY = moveY

                    returnAnimatorSet = AnimatorSet().apply {
                        playTogether(
                            ValueAnimator.ofFloat(playerScaleMoving, 1F).apply {
                                addUpdateListener {
                                    playerScaleCurrent = it.animatedValue as Float
                                    invalidate()
                                }
                            },
                            ValueAnimator.ofFloat(srcX, dstX).apply {
                                addUpdateListener {
                                    moveX = it.animatedValue as Float
                                }
                            },

                            ValueAnimator.ofFloat(srcY, dstY).apply {
                                addUpdateListener {
                                    moveY = it.animatedValue as Float
                                }
                            })

                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                movingEntry = null
                                moveY = 0F
                                moveX = 0F
                            }
                        })

                        duration = returnAnimationDuration
                        start()
                    }
                }

                return true
            }
        }

        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        singleCellSize = w.toFloat() / gridData.size
        playerRadius = singleCellSize * playerSize * 0.5F
        playerRadiusOutline = singleCellSize * playerOutlineSize * 0.5F

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > height) height else width
        setMeasuredDimension(size, size)
    }
}