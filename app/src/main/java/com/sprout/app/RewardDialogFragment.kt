package com.sprout.app

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.sprout.app.databinding.DialogRewardBinding

/**
 * Full-screen celebration shown whenever a child finishes a mini activity:
 * confetti animation + chime/jingle + a sticker emoji + simple praise text.
 * Used both when the doodle timer runs out and when a Photo Hunt item is found.
 */
class RewardDialogFragment : DialogFragment() {

    private var binding: DialogRewardBinding? = null
    private var onContinue: (() -> Unit)? = null

    companion object {
        private const val ARG_STICKER = "sticker"
        private const val ARG_TITLE = "title"
        private const val ARG_BODY = "body"

        fun newInstance(
            sticker: String,
            title: String,
            body: String,
            onContinue: (() -> Unit)? = null
        ): RewardDialogFragment {
            val frag = RewardDialogFragment()
            frag.arguments = Bundle().apply {
                putString(ARG_STICKER, sticker)
                putString(ARG_TITLE, title)
                putString(ARG_BODY, body)
            }
            frag.onContinue = onContinue
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Theme_Sprout_Dialog)
        val localBinding = DialogRewardBinding.inflate(layoutInflater)
        binding = localBinding
        dialog.setContentView(localBinding.root)
        isCancelable = false

        localBinding.txtSticker.text = arguments?.getString(ARG_STICKER) ?: "🌟"
        localBinding.txtTitle.text = arguments?.getString(ARG_TITLE) ?: ""
        localBinding.txtBody.text = arguments?.getString(ARG_BODY) ?: ""

        localBinding.btnContinue.setOnClickListener {
            dismiss()
            onContinue?.invoke()
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        binding?.confettiView?.start()
        (activity as? HasSoundBoard)?.soundBoard?.playSuccess()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.confettiView?.stop()
        binding = null
    }
}

/** Lets the dialog reach the host activity's shared SoundBoard without a new instance. */
interface HasSoundBoard {
    val soundBoard: SoundBoard
}
