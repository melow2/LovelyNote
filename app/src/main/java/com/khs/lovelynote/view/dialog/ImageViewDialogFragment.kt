package com.khs.lovelynote.view.dialog

import android.R
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.transition.TransitionInflater
import com.khs.lovelynote.databinding.DialogImageViewBinding
import com.khs.lovelynote.module.glide.GlideImageLoader
import com.khs.lovelynote.module.glide.ProgressAppGlideModule


class ImageViewDialogFragment() : DialogFragment() {

    lateinit var mBinding: DialogImageViewBinding

    companion object {
        const val ITEM_URI = "ITEM_URI"
        const val EXTRA_TRANSITION_NAME = "transition_name"
        fun newInstance(uri: String?, transitionName: String?): ImageViewDialogFragment {
            return ImageViewDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ITEM_URI, uri)
                    putString(EXTRA_TRANSITION_NAME, transitionName)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog == null || dialog?.window == null) return
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.CENTER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        setStyle(STYLE_NORMAL, com.khs.lovelynote.R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val uri = arguments?.getString(ITEM_URI)
        val transitionName = arguments?.getString(EXTRA_TRANSITION_NAME)
        mBinding = DialogImageViewBinding.inflate(LayoutInflater.from(context))
        mBinding.ivImage.transitionName = transitionName
        GlideImageLoader(mBinding.ivImage, null).load(
            uri,
            ProgressAppGlideModule.requestOptions(requireContext())
        )
        return mBinding.root
    }

}
