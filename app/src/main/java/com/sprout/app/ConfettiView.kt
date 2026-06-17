package com.sprout.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * Simple falling-confetti celebration animation, used whenever a child
 * completes a mini activity (drawing time's up, or a Photo Hunt find).
 */
class ConfettiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val colors = listOf(
        Color.parseColor("#FFC93C"),
        Color.parseColor("#FF8A5B"),
        Color.parseColor("#4DA8DA"),
        Color.parseColor("#9B6BCC"),
        Color.parseColor("#FF6F91"),
        Color.parseColor("#4CAF7D")
    )

    private data class Piece(
        var x: Float,
        var y: Float,
        var vy: Float,
        var rotation: Float,
        var rotationSpeed: Float,
        val size: Float,
        val color: Int
    )

    private val pieces = mutableListOf<Piece>()
    private val paint = Paint().apply { isAntiAlias = true }
    private var running = false

    fun start(pieceCount: Int = 60) {
        pieces.clear()
        repeat(pieceCount) {
            pieces.add(
                Piece(
                    x = Random.nextFloat() * width,
                    y = -Random.nextFloat() * height * 0.5f,
                    vy = Random.nextFloat() * 6f + 4f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 10f - 5f,
                    size = Random.nextFloat() * 10f + 8f,
                    color = colors.random()
                )
            )
        }
        running = true
        postOnAnimation(step)
    }

    fun stop() {
        running = false
        pieces.clear()
        invalidate()
    }

    private val step = object : Runnable {
        override fun run() {
            if (!running) return
            for (p in pieces) {
                p.y += p.vy
                p.rotation += p.rotationSpeed
                if (p.y > height) {
                    p.y = -20f
                    p.x = Random.nextFloat() * width
                }
            }
            invalidate()
            postOnAnimation(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (p in pieces) {
            paint.color = p.color
            canvas.save()
            canvas.translate(p.x, p.y)
            canvas.rotate(p.rotation)
            val half = p.size / 2
            canvas.drawRect(RectF(-half, -half * 0.6f, half, half * 0.6f), paint)
            canvas.restore()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        running = false
    }
}
