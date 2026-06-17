package com.sprout.app

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.sprout.app.databinding.ActivityDoodleBinding

/**
 * Task 2 deliverable: a single interactive screen for ages 3-5.
 * Tap interaction: choosing a color swatch, toggling sparkle mode, clearing.
 * Animation: sparkle particle trail while drawing + confetti reward screen.
 * Sound: a soft pop on swatch taps, a chime on the first stroke, and a
 * celebratory jingle when the drawing session ends.
 */
class DoodleActivity : AppCompatActivity(), HasSoundBoard {

    private lateinit var binding: ActivityDoodleBinding
    override lateinit var soundBoard: SoundBoard

    private val paletteColors = listOf(
        "#FF5C5C", "#FF8A5B", "#FFC93C", "#4CAF7D", "#4DA8DA", "#9B6BCC"
    )
    private var selectedSwatch: View? = null

    private var timer: CountDownTimer? = null
    private val sessionLengthMs = 60_000L // scaled-down demo of the 5-minute loop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoodleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundBoard = SoundBoard(this)

        binding.btnBack.setOnClickListener { finish() }

        setUpPalette()

        binding.btnSparkle.setOnClickListener {
            binding.doodleCanvas.sparkleMode = !binding.doodleCanvas.sparkleMode
            val on = binding.doodleCanvas.sparkleMode
            binding.btnSparkle.alpha = if (on) 1f else 0.55f
            if (on) soundBoard.playSparkle()
        }

        binding.btnClear.setOnClickListener {
            binding.doodleCanvas.clearCanvas()
            soundBoard.playPop()
        }

        binding.doodleCanvas.onFirstStroke = { soundBoard.playChime() }

        startTimer()
    }

    private fun setUpPalette() {
        paletteColors.forEachIndexed { index, hex ->
            val swatch = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(44.dp, 44.dp).apply {
                    if (index != 0) marginStart = 10.dp
                }
                background = createSwatchDrawable(Color.parseColor(hex), selected = index == 0)
                setOnClickListener { onSwatchTapped(this, Color.parseColor(hex)) }
            }
            binding.colorRow.addView(swatch)
            if (index == 0) {
                selectedSwatch = swatch
                binding.doodleCanvas.currentColor = Color.parseColor(hex)
            }
        }
    }

    private fun onSwatchTapped(view: View, color: Int) {
        binding.doodleCanvas.currentColor = color
        selectedSwatch?.scaleX = 1f
        selectedSwatch?.scaleY = 1f
        view.scaleX = 1.18f
        view.scaleY = 1.18f
        selectedSwatch = view
        soundBoard.playPop()
    }

    private fun createSwatchDrawable(color: Int, selected: Boolean): android.graphics.drawable.Drawable {
        val shape = android.graphics.drawable.GradientDrawable()
        shape.shape = android.graphics.drawable.GradientDrawable.OVAL
        shape.setColor(color)
        if (selected) shape.setStroke(3.dp, Color.parseColor("#33424B"))
        return shape
    }

    private fun startTimer() {
        timer = object : CountDownTimer(sessionLengthMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = (millisUntilFinished / 1000).toInt()
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                binding.txtTimer.text = String.format("%d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.txtTimer.text = "0:00"
                onSessionComplete()
            }
        }.start()
    }

    private fun onSessionComplete() {
        binding.btnClear.isEnabled = false
        binding.btnSparkle.isEnabled = false
        binding.doodleCanvas.isEnabled = false

        RewardDialogFragment.newInstance(
            sticker = "🎨",
            title = getString(R.string.doodle_time_up_title),
            body = getString(R.string.doodle_time_up_body)
        ) { finish() }.show(supportFragmentManager, "reward")
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        soundBoard.release()
    }
}

private val Int.dp: Int
    get() = (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
