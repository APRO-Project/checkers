package com.cyberbot.checkers.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
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
    }

    // <editor-fold defaultstate="collapsed" desc="Colors and paint">
    private var gridColor1: Int = 0
        set(value) {
            field = value
            paintGridColor1.color = value
            invalidate()
        }
    private var gridColor2: Int = 0
        set(value) {
            field = value
            paintGridColor2.color = value
            invalidate()
        }

    private var playerColor1: Int = 0
        set(value) {
            field = value
            paintPlayerColor1.color = value
            invalidate()
        }
    private var playerColor2: Int = 0
        set(value) {
            field = value
            paintPlayerColor2.color = value
            invalidate()
        }

    private var playerOutlineColor1: Int = 0
        set(value) {
            field = value
            paintPlayerOutlineColor1.color = value
            invalidate()
        }
    private var playerOutlineColor2: Int = 0
        set(value) {
            field = value
            paintPlayerOutlineColor2.color = value
            invalidate()
        }

    private val paintGridColor1 = Paint(0).apply {
        color = gridColor1
        style = Paint.Style.FILL
    }

    private val paintGridColor2 = Paint(0).apply {
        color = gridColor2
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

    var playerSize: Float = 0.7F
        set(value) {
            field = value
            invalidate()
        }
    var playerOutlineSize: Float = 0.8F
        set(value) {
            field = value
            invalidate()
        }

    var gridData = Grid(8, 3)

    var movingEntry: GridEntry? = null
    var moveOffsetX: Float = 0F
    var moveOffsetY: Float = 0F
    var moveX = -1F
    var moveY = -1F

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CheckersGridView,
            0, 0
        ).apply {
            try {
                gridColor1 = getColor(R.styleable.CheckersGridView_grid_color1, COLOR_DEFAULT_GRID)
                gridColor2 = getColor(R.styleable.CheckersGridView_grid_color2, Color.WHITE)
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

    private fun drawPlayer(canvas: Canvas, entry: GridEntry, cx: Float, cy: Float) {
        canvas.apply {
            when (entry.player) {
                PlayerNum.FIRST -> {
                    drawCircle(cx, cy, playerRadiusOutline, paintPlayerOutlineColor1)
                    drawCircle(cx, cy, playerRadius, paintPlayerColor1)
                }
                PlayerNum.SECOND -> {
                    drawCircle(cx, cy, playerRadiusOutline, paintPlayerOutlineColor2)
                    drawCircle(cx, cy, playerRadius, paintPlayerColor2)
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
                val left = it.x * singleCellSize
                val top = it.y * singleCellSize

                drawRect(
                    left,
                    top,
                    left + singleCellSize,
                    top + singleCellSize,
                    if (it.legal()) paintGridColor1 else paintGridColor2
                )

                if (it != movingEntry) {
                    val cx = (it.x + 0.5F) * singleCellSize
                    val cy = (it.y + 0.5F) * singleCellSize
                    drawPlayer(this, it, cx, cy)
                }
            }

           movingEntry?.let {
               drawPlayer(this, it, moveX - moveOffsetX, moveY - moveOffsetY)
           }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x / singleCellSize
                val y = event.y / singleCellSize

                val entry = gridData.getEntryByCoords(x.toInt(), y.toInt())
                if (entry.player == PlayerNum.NOPLAYER) {
                    return true
                }

                val cx = (entry.x + 0.5F) * singleCellSize
                val cy = (entry.y + 0.5F) * singleCellSize

                movingEntry = entry

                moveOffsetX = event.x - cx
                moveOffsetY = event.y - cy
                moveX = event.x
                moveY = event.y

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                moveX = event.x
                moveY = event.y

                invalidate()

                Log.d(this.javaClass.simpleName, "Moving ($moveX, $moveY)")

                return true
            }
            MotionEvent.ACTION_UP -> {
                moveOffsetX = 0F
                moveOffsetY = 0F
                movingEntry = null

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