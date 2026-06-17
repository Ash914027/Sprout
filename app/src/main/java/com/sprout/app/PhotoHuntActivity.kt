package com.sprout.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.sprout.app.databinding.ActivityPhotoHuntBinding

/**
 * Task 4 deliverable: live device camera integration for a "Photo Hunt"
 * learning activity ("Find 5 things around the house"). Captures a photo,
 * runs it through an on-device, fully-offline ML Kit image labeler to
 * identify what was found, then gives the child a reward (sticker + confetti
 * + chime) regardless of an exact match, so the experience always feels
 * encouraging for a 3-5 year old.
 */
class PhotoHuntActivity : AppCompatActivity(), HasSoundBoard {

    private lateinit var binding: ActivityPhotoHuntBinding
    override lateinit var soundBoard: SoundBoard

    private data class HuntTarget(val emoji: String, val label: String, val keywords: List<String>)

    private val targets = listOf(
        HuntTarget("🌼", "a flower", listOf("flower", "plant", "petal", "blossom")),
        HuntTarget("📕", "a book", listOf("book", "publication")),
        HuntTarget("🧸", "a toy", listOf("toy", "teddy", "stuffed")),
        HuntTarget("🥤", "a cup", listOf("cup", "mug", "glass", "drinkware")),
        HuntTarget("👟", "a shoe", listOf("shoe", "footwear", "sneaker", "boot"))
    )
    private var currentTargetIndex = 0
    private val dotViews = mutableListOf<android.view.View>()

    private var imageCapture: ImageCapture? = null
    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    private var processing = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onPermissionGranted() else showPermissionOverlay()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoHuntBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundBoard = SoundBoard(this)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnGrantPermission.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        binding.btnCapture.setOnClickListener { onCaptureTapped() }

        buildProgressDots()
        updatePrompt()

        if (hasCameraPermission()) onPermissionGranted() else showPermissionOverlay()
    }

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED

    private fun showPermissionOverlay() {
        binding.permissionOverlay.visibility = android.view.View.VISIBLE
    }

    private fun onPermissionGranted() {
        binding.permissionOverlay.visibility = android.view.View.GONE
        startCamera()
    }

    private fun buildProgressDots() {
        targets.forEachIndexed { index, _ ->
            val dot = android.view.View(this).apply {
                layoutParams = LinearLayout.LayoutParams(14.dp, 14.dp).apply {
                    if (index != 0) marginStart = 8.dp
                }
                background = ContextCompat.getDrawable(this@PhotoHuntActivity, R.drawable.dot_pending)
            }
            binding.progressDots.addView(dot)
            dotViews.add(dot)
        }
    }

    private fun updatePrompt() {
        if (currentTargetIndex >= targets.size) return
        val target = targets[currentTargetIndex]
        binding.txtPrompt.text = "${target.emoji}  Find ${target.label}!"
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val capture = ImageCapture.Builder().build()
            imageCapture = capture

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun onCaptureTapped() {
        if (processing || currentTargetIndex >= targets.size) return
        val capture = imageCapture ?: return
        processing = true
        binding.btnCapture.alpha = 0.5f

        capture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)
                    image.close()
                    if (bitmap != null) classify(bitmap) else finishProcessing()
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    finishProcessing()
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        return try {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            val raw = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
            val rotation = image.imageInfo.rotationDegrees
            if (rotation == 0) {
                raw
            } else {
                val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun classify(bitmap: Bitmap) {
        val input = InputImage.fromBitmap(bitmap, 0)
        imageLabeler.process(input)
            .addOnSuccessListener { labels -> onLabelsReady(labels) }
            .addOnFailureListener { finishProcessing() }
    }

    private fun onLabelsReady(labels: List<com.google.mlkit.vision.label.ImageLabel>) {
        val target = targets[currentTargetIndex]
        val matched = labels.firstOrNull { lbl ->
            target.keywords.any { kw -> lbl.text.lowercase().contains(kw) }
        }
        val anyLabel = labels.maxByOrNull { it.confidence }

        when {
            matched != null -> celebrateFind(target.emoji, "Yes! You found ${target.label}!")
            anyLabel != null && anyLabel.confidence > 0.4f ->
                celebrateFind("🔎", "Ooh, I see a ${anyLabel.text}! Nice exploring!")
            else -> {
                soundBoard.playPop()
                finishProcessing()
            }
        }
    }

    private fun celebrateFind(sticker: String, message: String) {
        currentTargetIndex++
        dotViews[currentTargetIndex - 1].background =
            ContextCompat.getDrawable(this, R.drawable.dot_found)

        val isLastFind = currentTargetIndex >= targets.size

        RewardDialogFragment.newInstance(
            sticker = sticker,
            title = message,
            body = if (isLastFind) getString(R.string.hunt_complete_body) else "Let's find the next one!"
        ) {
            finishProcessing()
            if (isLastFind) onHuntComplete() else updatePrompt()
        }.show(supportFragmentManager, "reward")
    }

    private fun onHuntComplete() {
        RewardDialogFragment.newInstance(
            sticker = "🏅",
            title = getString(R.string.hunt_complete_title),
            body = getString(R.string.hunt_complete_body)
        ) { finish() }.show(supportFragmentManager, "badge")
    }

    private fun finishProcessing() {
        processing = false
        binding.btnCapture.alpha = 1f
    }

    override fun onDestroy() {
        super.onDestroy()
        soundBoard.release()
        imageLabeler.close()
    }
}

private val Int.dp: Int
    get() = (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
