package com.sprout.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

/**
 * A simple finger-painting canvas for ages 3-5.
 *
 * Strokes are baked permanently into [drawingBitmap] so the drawing persists.
 * When [sparkleMode] is on, each touch-move also spawns short-lived glittering
 * [Particle]s that float and fade above the drawing - this is the
 * "tap interaction + animation" requirement for the activity.
 */
class DoodleCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var currentColor: Int = Color.parseColor("#FF5C5C")
    var sparkleMode: Boolean = false

    private val sparkleColors = listOf(
        Color.parseColor("#FFC93C"),
        Color.parseColor("#FF8A5B"),
        Color.parseColor("#4DA8DA"),
        Color.parseColor("#9B6BCC"),
        Color.parseColor("#FF6F91"),
        Color.parseColor("#4CAF7D")
    )

    private lateinit var drawingBitmap: Bitmap
    private lateinit var bitmapCanvas: Canvas

    private val strokePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 26f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val particlePaint = Paint().apply { isAntiAlias = true }

    private var lastX = 0f
    private var lastY = 0f
    private var hasStroke = false

    private val particles = mutableListOf<Particle>()
    private var animating = false

    /** Notified the first time the child actually draws something. */
    var onFirstStroke: (() -> Unit)? = null

    private data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var life: Float, // 1f -> 0f
        val color: Int,
        val size: Float
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmapCanvas = Canvas(drawingBitmap)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                strokePaint.color = currentColor
                bitmapCanvas.drawCircle(x, y, strokePaint.strokeWidth / 2, strokePaint)
                if (sparkleMode) spawnParticles(x, y)
                if (!hasStroke) {
                    hasStroke = true
                    onFirstStroke?.invoke()
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                strokePaint.color = currentColor
                bitmapCanvas.drawLine(lastX, lastY, x, y, strokePaint)
                if (sparkleMode) spawnParticles(x, y)
                lastX = x
                lastY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // nothing extra needed
            }
        }
        return true
    }

    private fun spawnParticles(x: Float, y: Float) {
        repeat(2) {
            particles.add(
                Particle(
                    x = x + Random.nextFloat() * 12 - 6,
                    y = y + Random.nextFloat() * 12 - 6,
                    vx = Random.nextFloat() * 2.4f - 1.2f,
                    vy = -(Random.nextFloat() * 2.5f + 0.8f),
                    life = 1f,
                    color = sparkleColors.random(),
                    size = Random.nextFloat() * 6f + 4f
                )
            )
        }
        startAnimatingIfNeeded()
    }

    private fun startAnimatingIfNeeded() {
        if (animating) return
        animating = true
        postOnAnimation(animStep)
    }

    private val animStep = object : Runnable {
        override fun run() {
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x += p.vx
                p.y += p.vy
                p.vy += 0.04f // gentle gravity so sparkles arc
                p.life -= 0.025f
                if (p.life <= 0f) iterator.remove()
            }
            invalidate()
            if (particles.isNotEmpty()) {
                postOnAnimation(this)
            } else {
                animating = false
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (::drawingBitmap.isInitialized) {
            canvas.drawBitmap(drawingBitmap, 0f, 0f, null)
        }
        for (p in particles) {
            particlePaint.color = p.color
            particlePaint.alpha = (p.life.coerceIn(0f, 1f) * 255).toInt()
            canvas.drawCircle(p.x, p.y, p.size * p.life.coerceIn(0.2f, 1f), particlePaint)
        }
    }

    fun clearCanvas() {
        if (::drawingBitmap.isInitialized) {
            drawingBitmap.eraseColor(Color.TRANSPARENT)
        }
        particles.clear()
        hasStroke = false
        invalidate()
    }
}
