package com.mobile.sportology.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class FootballFieldView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val arcRadius = 70f
    private val sweepAngle = 90f

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFootballField(canvas)
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