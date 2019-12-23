package com.cyberbot.checkers.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.cyberbot.checkers.R
import com.cyberbot.checkers.game.Grid
import com.cyberbot.checkers.game.PlayerNum


class CheckersGridView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {
    companion object{
        val COLOR_DEFAULT_GRID =  Color.rgb(0.5F, 0.5F, 0.5F)
    }

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

                val circleX = left + singleCellSize / 2
                val circleY = top + singleCellSize / 2

                when (it.player) {
                    PlayerNum.FIRST -> {
                        drawCircle(circleX, circleY, playerRadiusOutline, paintPlayerOutlineColor1)
                        drawCircle(circleX, circleY, playerRadius, paintPlayerColor1)
                    }
                    PlayerNum.SECOND -> {
                        drawCircle(circleX, circleY, playerRadiusOutline, paintPlayerOutlineColor2)
                        drawCircle(circleX, circleY, playerRadius, paintPlayerColor2)
                    }
                    PlayerNum.NOPLAYER -> {
                        // Do not draw
                    }
                }
            }
        }
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