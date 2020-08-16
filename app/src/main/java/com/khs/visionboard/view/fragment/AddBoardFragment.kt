package com.khs.visionboard.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardItemMediaAudioBinding
import com.khs.visionboard.databinding.BoardItemMediaImageBinding
import com.khs.visionboard.databinding.BoardItemMediaVideoBinding
import com.khs.visionboard.databinding.FragmentAddBoardBinding
import com.khs.visionboard.extension.*
import com.khs.visionboard.extension.Constants.AUDIO_ITEM_RANGE
import com.khs.visionboard.extension.Constants.TAG_AUDIO_DIALOG_FRAGMENT
import com.khs.visionboard.extension.Constants.DURATION_FADE_OUT
import com.khs.visionboard.extension.Constants.GALLERY_ITEM_RANGE
import com.khs.visionboard.extension.Constants.MEDIA_RCV_HEIGHT
import com.khs.visionboard.extension.Constants.SELECTED_ITEM_RANGE
import com.khs.visionboard.extension.Constants.VIDEO_ITEM_RANGE
import com.khs.visionboard.model.mediastore.*
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule
import com.khs.visionboard.view.activity.MediaStoreViewPagerActivity
import com.khs.visionboard.view.adapter.MediaAudioPagedAdapter
import com.khs.visionboard.view.adapter.MediaImagePagedAdapter
import com.khs.visionboard.view.adapter.MediaVideoPagedAdapter
import com.khs.visionboard.view.adapter.SelectedMediaFileListAdapter
import com.khs.visionboard.view.dialog.AudioPlayDialogFragment
import com.khs.visionboard.viewmodel.BoardAddVM
import com.khs.visionboard.viewmodel.factory.BoardAddVMFactory
import timber.log.Timber


class AddBoardFragment : BaseFragment<FragmentAddBoardBinding>(),
    MediaImagePagedAdapter.MediaPagedImageListener, MediaAudioPagedAdapter.MediaPagedAudioListener,
    MediaVideoPagedAdapter.MediaPagedViedoListener,
    SelectedMediaFileListAdapter.SelectedImageListEvent {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var boardAddVM: BoardAddVM
    private lateinit var mediaImagePagedAdapter: MediaImagePagedAdapter
    private lateinit var mediaAudioPagedAdapter: MediaAudioPagedAdapter
    private lateinit var mediaVideoPagedAdapter: MediaVideoPagedAdapter
    private lateinit var selectedListAdapter: SelectedMediaFileListAdapter

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

    /**
     * 미디어스토어에서 읽은 이미지 파일 observer
     * 1) 프래그먼트 생성시 초기화.
     * 2) 사실 상 옵저버 기능은 하지 않고, 최초에 읽어오는 것으로 구현.
     * */
    private val observerMediaStoreImage: Observer<PagedList<MediaStoreImage>> =
        Observer { mediaStores: PagedList<MediaStoreImage> ->
            mediaStores.let {
                mediaImagePagedAdapter.submitList(mediaStores)
            }
        }

    /**
     * 미디어스토에서 읽은 오디오 파일 observer
     * 1) 프래그먼트 생성시 초기화.
     * 2) 사실 상 옵저버 기능은 하지 않고, 최초에 읽어오는 것으로 구현.
     * */
    private val observerMediaStoreAudio: Observer<PagedList<MediaStoreAudio>> =
        Observer { mediaStores: PagedList<MediaStoreAudio> ->
            mediaStores.let {
                mediaAudioPagedAdapter.submitList(mediaStores)
            }
        }

    /**
     * 미디어스토에서 읽은 비디오 파일 observer
     * 1) 프래그먼트 생성시 초기화.
     * 2) 사실 상 옵저버 기능은 하지 않고, 최초에 읽어오는 것으로 구현.
     * */
    private val observerMediaStoreVideo: Observer<PagedList<MediaStoreVideo>> =
        Observer { mediaStores: PagedList<MediaStoreVideo> ->
            mediaStores.let {
                mediaVideoPagedAdapter.submitList(mediaStores)
            }
        }


    /**
     * 리사이클러뷰에서 선택한 이미지 observer
     *
     * 1) 선택한 이미지가 1개 이상이라면, 버튼에 애니메이션 추가.
     * */
    private val observerSelectedMediaStoreItem: Observer<List<SelectedMediaStoreItem>> =
        Observer { selectedMediaStoreItems: List<SelectedMediaStoreItem> ->
            mBinding?.apply {
                if (selectedMediaStoreItems.isNotEmpty() && btnRefresh.visibility == View.GONE) {
                    btnRefresh.fadeInAnimation()
                    ivThumbnail.fadeInAnimation()

                } else if (selectedMediaStoreItems.isEmpty() && btnRefresh.visibility == View.VISIBLE) {
                    btnRefresh.fadeOutAnimation()
                }
                // 썸네일 저장.
                if (!selectedMediaStoreItems.isNullOrEmpty() && ivThumbnail.visibility == View.VISIBLE) {
                    GlideImageLoader(ivThumbnail, null).load(
                        (selectedMediaStoreItems[0].selectedItem.contentUri.toString()),
                        ProgressAppGlideModule.requestOptions(requireActivity())
                    )
                } else {
                    if (rootMediaAddedList.visibility == View.VISIBLE)
                        rootMediaLayout.collapseAnimation(DURATION_FADE_OUT, 0)
                }
                selectedListAdapter.submitList(selectedMediaStoreItems)
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
//      val context = view.context
        setUpListener()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        mBinding?.apply {
            rcvMediaImageList.apply {
                mediaImagePagedAdapter =
                    MediaImagePagedAdapter(context).apply { addListener(this@AddBoardFragment) }
                layoutManager = GridLayoutManager(requireActivity(), GALLERY_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = mediaImagePagedAdapter
            }
            rcvMediaAudioList.apply {
                mediaAudioPagedAdapter =
                    MediaAudioPagedAdapter(context).apply { addListener(this@AddBoardFragment) }
                layoutManager = GridLayoutManager(requireActivity(), GALLERY_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = mediaAudioPagedAdapter
            }

            rcvMediaVideoList.apply {
                mediaVideoPagedAdapter =
                    MediaVideoPagedAdapter(context).apply { addListener(this@AddBoardFragment) }
                layoutManager = GridLayoutManager(requireActivity(), GALLERY_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = mediaVideoPagedAdapter
            }

            rcvMediaAdded.apply {
                selectedListAdapter =
                    SelectedMediaFileListAdapter(context).apply { addEventListener(this@AddBoardFragment) }
                layoutManager = GridLayoutManager(requireActivity(), SELECTED_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = selectedListAdapter
            }
        }
    }

    /**
     * todo 1) 라이프사이클 사용시 옵저버가 삭제되는 시점을 정확하게 알아야 함.
     * 확인해본 결과, 액티비티가 onDestroy() 되면서 옵저버도 모두 삭제되는 것으로 확인.
     * */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Timber.d("onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        boardAddVM =
            ViewModelProvider(this, BoardAddVMFactory(requireActivity().application, 100)).get(
                BoardAddVM::class.java
            )
        boardAddVM.apply {
            getImages().observeOnce(viewLifecycleOwner, observerMediaStoreImage)
            getAudios().observeOnce(viewLifecycleOwner, observerMediaStoreAudio)
            getSelectedObjectList().observe(viewLifecycleOwner, observerSelectedMediaStoreItem)
        }
        this.lifecycle.addObserver(boardAddVM)
    }

    /**
     * 1) TABLE_RANGE_COUNT를 전역변수처럼 쓸 수 있음.
     * */
    private fun setUpListener() {
        var imageTableCounter: Int = GALLERY_ITEM_RANGE
        var audioTableCounter: Int = AUDIO_ITEM_RANGE
        var videoTableCounter: Int = VIDEO_ITEM_RANGE
        mBinding?.apply {
            /***************************************************************************
             * 메인 버튼 ( 파일, 비디오, 오디오, 카메라, 미디어스토어)
             * */
            // 오디오 버튼.
            btnRecord.setOnClickListener {
                showToast(requireActivity(), "DDDDDD")
            }
            // 카메라 버튼
            btnCamera.setOnClickListener {
                showToast(requireActivity(), "adfadfadfadf")
            }

            // 미디어 스토어 버튼.
            btnMediaStore.setOnClickListener {
                rootMediaLayout.expandAnimation(DURATION_FADE_OUT, MEDIA_RCV_HEIGHT)
                rootMediaBtnLayout.fadeInAnimation()
                rootMainBtn.fadeOutAnimation()
                rootMediaAddedList.fadeOutAnimation()
                rootMediaFileList.fadeInAnimation()
            }

            /***************************************************************************
             * 미디어스토어 버튼 ( 사진, 비디오, 오디오, 선택해제, 저장 )
             * 1) 미디어스토어를 항상 옵저버링 할 수 없음.
             * 2) 액티비티 생성 주기에 맞게(create)에 초기화.
             * */

            btnMediaVideo.setOnClickListener {
                if (rcvMediaVideoList.visibility == View.GONE) rcvMediaVideoList.fadeInAnimation()
                if (rcvMediaAudioList.visibility == View.VISIBLE) rcvMediaAudioList.visibility =
                    View.GONE
                if (rcvMediaImageList.visibility == View.VISIBLE) rcvMediaImageList.visibility =
                    View.GONE
                boardAddVM.getVedios().observeOnce(viewLifecycleOwner, observerMediaStoreVideo)
            }

            btnMediaImage.setOnClickListener {
                if (rcvMediaImageList.visibility == View.GONE) rcvMediaImageList.fadeInAnimation()
                if (rcvMediaAudioList.visibility == View.VISIBLE) rcvMediaAudioList.visibility =
                    View.GONE
                if (rcvMediaVideoList.visibility == View.VISIBLE) rcvMediaVideoList.visibility =
                    View.GONE
                boardAddVM.getImages().observeOnce(viewLifecycleOwner, observerMediaStoreImage)
            }

            btnMediaRecord.setOnClickListener {
                if (rcvMediaAudioList.visibility == View.GONE) rcvMediaAudioList.fadeInAnimation()
                if (rcvMediaImageList.visibility == View.VISIBLE) rcvMediaImageList.visibility =
                    View.GONE
                if (rcvMediaVideoList.visibility == View.VISIBLE) rcvMediaVideoList.visibility =
                    View.GONE
                boardAddVM.getAudios().observeOnce(viewLifecycleOwner, observerMediaStoreAudio)
            }

            // 새로고침 버튼.
            btnRefresh.setOnClickListener {
                boardAddVM.apply {
                    removeAllSelectedItemAnimation()
                    mediaAudioPagedAdapter.initSelectedItems()
                    mediaImagePagedAdapter.initSelectedItems()
                    mediaVideoPagedAdapter.initSelectedItems()
                }
            }

            // 아이템 추가 버튼.
            btnItemAdd.setOnClickListener {
                rootMediaBtnLayout.fadeOutAnimation()
                rootMainBtn.fadeInAnimation()
                rootMediaFileList.fadeOutAnimation()
                rcvMediaAudioList.visibility = View.GONE
                rcvMediaVideoList.visibility = View.GONE
                rcvMediaImageList.visibility = View.VISIBLE
                boardAddVM.run {
                    getSelectedObjectList().value.run {
                        if (this.isNullOrEmpty()) rootMediaLayout.collapseAnimation(
                            DURATION_FADE_OUT,
                            0
                        )
                        else rootMediaAddedList.fadeInAnimation()
                    }
                }
            }

            // 리싸이클러뷰 뷰 범위 변경.
            btnTablePlus.setOnClickListener {
                var targetRangeCounter: Int = GALLERY_ITEM_RANGE
                when {
                    rcvMediaImageList.visibility == View.VISIBLE -> {
                        if (imageTableCounter < 5) ++imageTableCounter
                        else {
                            imageTableCounter = GALLERY_ITEM_RANGE
                        }
                        targetRangeCounter = imageTableCounter
                    }
                    rcvMediaAudioList.visibility == View.VISIBLE -> {
                        if (audioTableCounter < 3) ++audioTableCounter
                        else {
                            audioTableCounter = AUDIO_ITEM_RANGE
                        }
                        targetRangeCounter = audioTableCounter
                    }
                    rcvMediaVideoList.visibility == View.VISIBLE -> {
                        if (videoTableCounter < 4) ++videoTableCounter
                        else {
                            videoTableCounter = VIDEO_ITEM_RANGE
                        }
                        targetRangeCounter = videoTableCounter
                    }
                }
                changedReyclerItemRange(targetRangeCounter)
            }
        }
    }

    /**
     * 리사이클러뷰에서 보여줄 수 있는 이미지의 갯수를 결정하는 메소드.
     *
     * @param range: 이미지 갯수.
     * */
    private fun changedReyclerItemRange(range: Int) {
        mBinding?.run {
            if (rcvMediaImageList.visibility == View.VISIBLE) {
                rcvMediaImageList.layoutManager = GridLayoutManager(requireActivity(), range)
            } else if (rcvMediaAudioList.visibility == View.VISIBLE) {
                rcvMediaAudioList.layoutManager = GridLayoutManager(requireActivity(), range)
            } else if (rcvMediaVideoList.visibility == View.VISIBLE) {
                rcvMediaVideoList.layoutManager = GridLayoutManager(requireActivity(), range)
            }
        }
    }

    /**
     * 리사이클러뷰의 이미지를 선택하는 메소드.
     *
     * @param binding: 선택한 파일의 데이터 바인딩. -> 애니메이션 초기화를 위해 필요함.
     * @param adapterPosition: 선택한 이미지의 어댑터 포지션 위치.
     * @param item: 선택한 파일의 데이터
     * @param checked: 선택한 파일의 선택여부( 선택:true, 해제:false )
     * @param type: 선택한 파일의 미디어 타입.
     * */
    override fun onMediaImageClickEvent(
        binding: BoardItemMediaImageBinding,
        adapterPosition: Int,
        item: MediaStoreImage,
        type: MediaStoreFileType,
        checked: Boolean
    ) {
        addSelectedMediaStoreItem(binding, adapterPosition, item, type, checked)
    }

    override fun onMediaAudioClickEvent(
        binding: BoardItemMediaAudioBinding,
        adapterPosition: Int,
        item: MediaStoreAudio,
        type: MediaStoreFileType,
        checked: Boolean
    ) {
        addSelectedMediaStoreItem(binding, adapterPosition, item, type, checked)
    }

    override fun onMediaVideoClickEvent(
        binding: BoardItemMediaVideoBinding,
        adapterPosition: Int,
        item: MediaStoreVideo,
        type: MediaStoreFileType,
        checked: Boolean
    ) {
        addSelectedMediaStoreItem(binding, adapterPosition, item, type, checked)
    }

    private fun addSelectedMediaStoreItem(
        binding: ViewDataBinding,
        adapterPosition: Int,
        item: MediaStoreItem,
        type: MediaStoreFileType,
        checked: Boolean
    ) {
        val selectedItem: SelectedMediaStoreItem? = SelectedMediaStoreItem(binding, SelectedItem(adapterPosition, item.contentUri, type, item))
            when (checked) {
                true -> {
                    boardAddVM.addSelectedItem(selectedItem)
                }
                false -> {
                    boardAddVM.removeSelectedItem(selectedItem)
                }
            }

    }

    override fun onClickSelectedItem(selectedItem: SelectedMediaStoreItem) {
        startActivity(
            MediaStoreViewPagerActivity.getStartIntent(
                requireContext(),
                boardAddVM.getAllSelectedItemList(),
                boardAddVM.getSelectedIndexOf(selectedItem)
            )
        )
    }

    override fun onMediaAudioPlayClientEvent(item: MediaStoreAudio?) {
        val ft = parentFragmentManager.beginTransaction()
        val prev = parentFragmentManager.findFragmentByTag(TAG_AUDIO_DIALOG_FRAGMENT)
        prev?.let {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val audioPlayDialog = AudioPlayDialogFragment.newInstance(item)
        audioPlayDialog.show(ft, TAG_AUDIO_DIALOG_FRAGMENT)
    }

    // 클릭했을 때 뭘 하나 덜지우면, 깜빡거린다.
    override fun onDeleteSelectedItem(selectedItem: SelectedMediaStoreItem) {
        boardAddVM.run {
            when (selectedItem.selectedItem.type) {
                MediaStoreFileType.IMAGE -> mediaImagePagedAdapter.removeSelectedItem(selectedItem.selectedItem.position)
                MediaStoreFileType.AUDIO -> mediaAudioPagedAdapter.removeSelectedItem(selectedItem.selectedItem.position)
                MediaStoreFileType.VIDEO -> mediaVideoPagedAdapter.removeSelectedItem(selectedItem.selectedItem.position)
            }
            removelSelectedItemAnimation(selectedItem.selectedItem)
            removeSelectedItem(selectedItem)
        }
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
