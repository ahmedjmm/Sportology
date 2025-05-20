package com.dev.goalpulse.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.dev.goalpulse.models.football.MatchPositions

class FootballFieldView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val arcRadius = 70f
    private val sweepAngle = 90f

    private var teamPositions: List<MatchPositions.MatchPositionsItem.Position?> = emptyList()


    private val fieldPaint = Paint().apply {
        color = Color.GREEN
    }

    private val linePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val circlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val teamPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val playerTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFootballField(canvas)
        drawPlayers(canvas)
    }

    private fun drawPlayer(
        canvas: Canvas?,
        xPercent: Double,
        yPercent: Double,
        paint: Paint,
        name: String?
    ) {
        val x = (width * xPercent / 100).toFloat()
        val y = (height * yPercent / 100).toFloat()

        // Draw player circle
        canvas?.drawCircle(x, y, 20f, paint)

        // Draw player name (first letter of first name + last name)
        val displayName = name?.split(" ")?.let {
            if (it.size > 1) "${it[0].first()}. ${it.last()}" else name
        } ?: ""

        canvas?.drawText(displayName, x, y - 25f, playerTextPaint)
    }

    private fun drawPlayers(canvas: Canvas?) {
        // Draw team players
        teamPositions.forEach { position ->
            position!!.averageX?.let { x ->
                position.averageY?.let { y ->
                    drawPlayer(canvas, x, y, teamPaint, position.playerName)
                }
            }
        }
    }

    fun setPlayerPositions(
        teamPositions: List<MatchPositions.MatchPositionsItem.Position?>,
        ) {
        this.teamPositions = teamPositions
        invalidate()
    }

    private fun drawFootballField(canvas: Canvas?) {
        // Draw the football field background
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fieldPaint)

        // Draw center line
        canvas?.drawLine(0f, height / 2f, width.toFloat(), height / 2f, linePaint)

        // Draw center circle
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = 100f
        canvas?.drawCircle(centerX, centerY, radius, circlePaint)

        val goalWidth = 100f
        val goalHeight = 50f
        // Top goal
        canvas?.drawRect(
            (width - goalWidth) / 2f,
            0f,
            (width + goalWidth) / 2f,
            goalHeight,
            linePaint
        )
        // Bottom goal
        canvas?.drawRect(
            (width - goalWidth) / 2f,
            height - goalHeight,
            (width + goalWidth) / 2f,
            height.toFloat(),
            linePaint
        )

        val penaltyWidth = 300f
        val penaltyHeight = 150f
        // Top penalty area
        canvas?.drawRect(
            (width - penaltyWidth) / 2f,
            0f,
            (width + penaltyWidth) / 2f,
            penaltyHeight,
            linePaint
        )
        // Bottom penalty area
        canvas?.drawRect(
            (width - penaltyWidth) / 2f,
            height - penaltyHeight,
            (width + penaltyWidth) / 2f,
            height.toFloat(),
            linePaint
        )

        // Top-left corner
        drawArcAtIntersection(
            canvas,
            startX = -arcRadius,
            startY = -arcRadius,
            startAngle = 0f,
        )

        // Top-right corner
        drawArcAtIntersection(
            canvas,
            startX = width - arcRadius,
            startY = -arcRadius,
            startAngle = 90f
        )

        // Bottom-left corner
        drawArcAtIntersection(
            canvas,
            startX = -arcRadius,
            startY = height - arcRadius,
            startAngle = 270f
        )

        // Bottom-right corner
        drawArcAtIntersection(
            canvas,
            startX = width - arcRadius,
            startY = height - arcRadius,
            startAngle = 180f
        )
    }

    private fun drawArcAtIntersection(
        canvas: Canvas?,
        startX: Float,
        startY: Float,
        startAngle: Float,
    ) {
        canvas?.drawArc(
            RectF(startX, startY, startX + 2 * arcRadius, startY + 2 * arcRadius),
            startAngle,
            sweepAngle,
            true,
            linePaint
        )
    }
}