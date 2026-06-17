package com.sprout.app

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Tiny wrapper around SoundPool so activities can fire short, kid-friendly
 * sound effects (pop / chime / success / sparkle) with near-zero latency.
 * All sounds are simple synthesized tones bundled in res/raw - no network
 * or external assets required, so the app works fully offline.
 */
class SoundBoard(context: Context) {

    private val soundPool: SoundPool
    private val pop: Int
    private val chime: Int
    private val success: Int
    private val sparkle: Int

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attrs)
            .build()

        pop = soundPool.load(context, R.raw.pop, 1)
        chime = soundPool.load(context, R.raw.chime, 1)
        success = soundPool.load(context, R.raw.success, 1)
        sparkle = soundPool.load(context, R.raw.sparkle, 1)
    }

    fun playPop() = soundPool.play(pop, 1f, 1f, 1, 0, 1f)
    fun playChime() = soundPool.play(chime, 1f, 1f, 1, 0, 1f)
    fun playSuccess() = soundPool.play(success, 1f, 1f, 1, 0, 1f)
    fun playSparkle() = soundPool.play(sparkle, 1f, 1f, 1, 0, 1f)

    fun release() = soundPool.release()
}
