package com.khs.visionboard.view.fragment

import android.content.Context
import android.os.Bundle
import android.transition.Fade
import android.view.*
import android.view.animation.AlphaAnimation
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.khs.visionboard.R
import com.khs.visionboard.databinding.FragmentAddBoardBinding
import com.khs.visionboard.model.gallery.GalleryImageItem
import com.khs.visionboard.model.gallery.GalleryImageItem.Companion.getGalleryItems
import com.khs.visionboard.model.gallery.PhotoItem
import com.khs.visionboard.util.collapse
import com.khs.visionboard.util.expand
import com.khs.visionboard.util.fadeInAnimation
import com.khs.visionboard.util.fadeOutAnimation
import com.khs.visionboard.view.adapter.GalleryPagedAdapter
import com.khs.visionboard.viewmodel.BoardAddVM
import com.khs.visionboard.viewmodel.factory.BoardAddVMFactory
import timber.log.Timber


class AddBoardFragment : BaseFragment<FragmentAddBoardBinding>() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var boardAddVM: BoardAddVM
    private var galleryPagedAdapter: GalleryPagedAdapter? = null

    private val observerGallery: Observer<PagedList<PhotoItem>> =
        Observer { photos: PagedList<PhotoItem> ->
            photos.let {
                Timber.d(photos.toString())
                galleryPagedAdapter?.submitList(photos)
            }
        }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddBoardFragment().apply {

                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_add_menu, menu)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindView(inflater, container!!, R.layout.fragment_add_board)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        galleryPagedAdapter = GalleryPagedAdapter(context)
        mBinding?.rcvGalleryList?.run {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = galleryPagedAdapter
        }
        setUpListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        boardAddVM = ViewModelProvider(this, BoardAddVMFactory(requireActivity().application, 100)).get(BoardAddVM::class.java)
    }

    private fun setUpListener() {
        mBinding?.btnGallery?.setOnClickListener {
            when (mBinding?.rootGalleryList?.visibility) {
                View.GONE -> {
                    mBinding?.rootImageList?.fadeOutAnimation()
                    mBinding?.rootGalleryList?.fadeInAnimation()
                    mBinding?.btnCamera?.fadeOutAnimation()                                     // 카메라 버튼.
                    mBinding?.btnGallery?.setImageResource(R.drawable.check_bold)
                    boardAddVM.getImages().observe(viewLifecycleOwner, observerGallery)// 갤러리 버튼.
                }
                else -> {
                    mBinding?.rootImageList?.fadeInAnimation()
                    mBinding?.rootGalleryList?.fadeOutAnimation()
                    mBinding?.btnCamera?.fadeInAnimation()                                      // 카메라 버튼.
                    mBinding?.btnGallery?.setImageResource(R.drawable.camera_image)     // 갤러리 버튼.
                    boardAddVM.getImages().removeObserver(observerGallery)
                }
            }
        }

        // 갤러리 사진 클릭 리스너.
        galleryPagedAdapter?.addListener(object : GalleryPagedAdapter.GalleryPagedListener {
            override fun onClickEvent(
                position: Int,
                item: PhotoItem?
            ) {
                getGalleryItems()[position] = GalleryImageItem(position,item?.imageDataPath?.toUri())
                Timber.d(getGalleryItems().toString())
            }

            override fun onClickLongEvent(
                position: Int,
                item: PhotoItem?
            ) {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}