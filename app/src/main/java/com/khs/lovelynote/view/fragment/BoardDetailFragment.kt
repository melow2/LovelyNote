package com.khs.lovelynote.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.khs.audiorecorder.AudioListener
import com.khs.audiorecorder.AudioRecording
import com.khs.audiorecorder.RecordingItem
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.*
import com.khs.lovelynote.extension.*
import com.khs.lovelynote.extension.Constants.TAG_MEDIA_ITEM_SIZE
import com.khs.lovelynote.extension.Constants.TAG_NOTE_ID
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.model.mediastore.*
import com.khs.lovelynote.module.glide.GlideImageLoader
import com.khs.lovelynote.module.glide.ProgressAppGlideModule
import com.khs.lovelynote.view.activity.ExoPlayerActivity
import com.khs.lovelynote.view.activity.MainActivity
import com.khs.lovelynote.view.activity.MediaStoreViewPagerActivity
import com.khs.lovelynote.view.adapter.MediaAudioPagedAdapter
import com.khs.lovelynote.view.adapter.MediaImagePagedAdapter
import com.khs.lovelynote.view.adapter.MediaVideoPagedAdapter
import com.khs.lovelynote.view.adapter.SelectedMediaFileListAdapter
import com.khs.lovelynote.view.behavior.KeyBoardActionBehavior
import com.khs.lovelynote.view.dialog.AudioPlayDialogFragment
import com.khs.lovelynote.viewmodel.BoardDetailVM
import com.khs.lovelynote.viewmodel.factory.BoardDetailVMFactory
import timber.log.Timber
import java.io.File
import java.util.*

class BoardDetailFragment : BaseFragment<BoardDetailDataBinding>(),
    MediaImagePagedAdapter.MediaPagedImageListener, MediaAudioPagedAdapter.MediaPagedAudioListener,
    MediaVideoPagedAdapter.MediaPagedViedoListener,
    SelectedMediaFileListAdapter.SelectedImageListEvent {

    private lateinit var boardDetailVM: BoardDetailVM
    private lateinit var mediaImagePagedAdapter: MediaImagePagedAdapter
    private lateinit var mediaAudioPagedAdapter: MediaAudioPagedAdapter
    private lateinit var mediaVideoPagedAdapter: MediaVideoPagedAdapter
    private lateinit var selectedListAdapter: SelectedMediaFileListAdapter
    private lateinit var audioRecording: AudioRecording
    private lateinit var mActivity: MainActivity
    private val handler = Handler(Looper.getMainLooper())
    private var mNoteId: Long = 0L
    private var mMediaItemSize: Int = 0
    private val currentDate = Date()

    companion object {
        @JvmStatic
        fun newInstance(noteId: Long, mediaItemSize: Int) =
            BoardDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(TAG_NOTE_ID, noteId)
                    putInt(TAG_MEDIA_ITEM_SIZE,mediaItemSize)
                }
            }
    }

    private val observerMainNote: Observer<LovelyNote?> =
        Observer { note: LovelyNote? ->
            note?.let {
                mBinding?.note = note
                val mediaItems = note._mediaItems
                mediaItems?.let {
                    lateinit var mediaItem: MediaStoreItem
                    for (item in it) {
                        when (item.type) {
                            MediaStoreFileType.IMAGE -> {
                                mediaItem = MediaStoreImage(
                                    item.id,
                                    item.dateTaken,
                                    item.displayName,
                                    item.contentUri,
                                    item.type
                                )
                            }
                            MediaStoreFileType.VIDEO -> {
                                mediaItem = MediaStoreVideo(
                                    item.id,
                                    item.dateTaken,
                                    item.displayName,
                                    item.contentUri,
                                    item.type,
                                    context?.getMediaMetaData(
                                        Uri.parse(item.contentUri),
                                        MediaMetadataRetriever.METADATA_KEY_DURATION
                                    )
                                )
                            }
                            MediaStoreFileType.AUDIO -> {
                                mediaItem = MediaStoreAudio(
                                    item.id,
                                    item.dateTaken,
                                    item.displayName,
                                    item.contentUri,
                                    item.type,
                                    context?.getMediaMetaData(
                                        Uri.parse(item.contentUri),
                                        MediaMetadataRetriever.METADATA_KEY_ALBUM
                                    ),
                                    context?.getMediaMetaData(
                                        Uri.parse(item.contentUri),
                                        MediaMetadataRetriever.METADATA_KEY_TITLE
                                    ),
                                    context?.getMediaMetaData(
                                        Uri.parse(item.contentUri),
                                        MediaMetadataRetriever.METADATA_KEY_DURATION
                                    )
                                )
                            }
                            MediaStoreFileType.FILE -> {
                                mediaItem = MediaStoreFile(
                                    item.id,
                                    item.dateTaken,
                                    item.displayName,
                                    item.contentUri,
                                    item.type
                                )
                            }
                        }
                        addSelectedMediaStoreItem(null, null, mediaItem, mediaItem.type, true)
                    }
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
                    thumbnailLyt.fadeInAnimation()
                } else if (selectedMediaStoreItems.isEmpty() && btnRefresh.visibility == View.VISIBLE) {
                    btnRefresh.fadeOutAnimation()
                }
                // 썸네일 저장.
                if (!selectedMediaStoreItems.isNullOrEmpty() && thumbnailLyt.visibility == View.VISIBLE) {
                    thumbnailLyt.addView(Thumbnail(target = selectedMediaStoreItems[0]))
                } else {
                    if (rootMediaAddedList.visibility == View.VISIBLE) {
                        thumbnailLyt.removeAllViews()
                        rootMediaLayout.collapseAnimation(Constants.DURATION_FADE_OUT, 0)
                    }
                }
                selectedListAdapter.submitList(selectedMediaStoreItems)
            }
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.toolbar_menu_detail, menu)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            mNoteId = it.getLong(TAG_NOTE_ID)
            mMediaItemSize = it.getInt(TAG_MEDIA_ITEM_SIZE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindView(inflater, container!!, R.layout.fragment_board_detail)
        mActivity = activity as MainActivity
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setUpListener()
        setUpRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        boardDetailVM = ViewModelProvider(
            this,
            BoardDetailVMFactory(requireActivity().application, mNoteId)
        ).get(BoardDetailVM::class.java)
        boardDetailVM.apply {
            getLovelyNote().observeOnce(viewLifecycleOwner, observerMainNote)
            getImages().observeOnce(viewLifecycleOwner, observerMediaStoreImage)
            getAudios().observeOnce(viewLifecycleOwner, observerMediaStoreAudio)
            getSelectedObjectList().observe(viewLifecycleOwner, observerSelectedMediaStoreItem)
        }
        this.lifecycle.addObserver(boardDetailVM)
    }

    private fun initView() {
        mBinding?.apply {
            if(mMediaItemSize>0){
                rootMediaLayout.expandAnimation(
                    Constants.DURATION_FADE_OUT,
                    Constants.MEDIA_RCV_HEIGHT
                )
                rootMediaAddedList.fadeInAnimation()
            }
            edtContent?.onFocusChangeListener = KeyBoardActionBehavior(requireActivity()).focusChangeListener
        }
    }

    /**
     * 1) TABLE_RANGE_COUNT를 전역변수처럼 쓸 수 있음.
     * */
    private fun setUpListener() {

        var imageTableCounter: Int = Constants.GALLERY_ITEM_RANGE
        var audioTableCounter: Int = Constants.AUDIO_ITEM_RANGE
        var videoTableCounter: Int = Constants.VIDEO_ITEM_RANGE
        audioRecording = AudioRecording(context)

        mBinding?.apply {
            /***************************************************************************
             * 메인 버튼 ( 파일, 비디오, 오디오, 카메라, 미디어스토어)
             * */
            // 파일 버튼.
            btnFile.setOnClickListener {
                rootMediaLayout.expandAnimation(
                    Constants.DURATION_FADE_OUT,
                    Constants.MEDIA_RCV_HEIGHT
                )
                startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).apply {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }, Constants.RC_GET_CONTENT)
            }

            // 비디오 버튼.
            btnVideo.setOnClickListener {
                rootMediaLayout.expandAnimation(
                    Constants.DURATION_FADE_OUT,
                    Constants.MEDIA_RCV_HEIGHT
                )
                startActivityForResult(requireContext().openVideoIntent(), Constants.RC_GET_VIDEO)
            }

            // 오디오 버튼.
            btnRecord.setOnAudioListener(object : AudioListener {
                private var isRecording: Boolean = false
                private val RECORD = "Record"
                override fun onError(e: Exception?) {}
                override fun onStart() {
                    if (rootMediaLayout.height == 0) {
                        rootMediaLayout.expandAnimation(
                            Constants.DURATION_FADE_OUT,
                            Constants.MEDIA_RCV_HEIGHT
                        )
                        showToast(requireContext(), "Click and hold the microphone button.")
                        isRecording = false
                        return
                    }
                    rootMediaAddedList.fadeOutAnimation()
                    lytAudioChronometer.fadeInAnimation()
                    audioChronometer.base = SystemClock.elapsedRealtime()
                    audioChronometer.start()
                    isRecording = true
                }

                override fun onStop(recordingItem: RecordingItem?) {
                    if (recordingItem != null && isRecording) {
                        val uri = Uri.parse(recordingItem.filePath)
                        MediaStoreAudio(
                            System.currentTimeMillis(),
                            Date(),
                            fileNameTimeStamp() + "_" + RECORD + ".mp3",
                            uri.toString(),
                            MediaStoreFileType.AUDIO,
                            RECORD,
                            System.currentTimeMillis().toString() + "_" + RECORD,
                            requireContext().getMediaMetaData(
                                uri,
                                MediaMetadataRetriever.METADATA_KEY_DURATION
                            )
                        ).apply {
                            addSelectedMediaStoreItem(null, null, this, this.type, true)
                            audioChronometer.stop()
                            lytAudioChronometer.fadeOutAnimation()
                            rootMediaAddedList.fadeInAnimation()
                        }
                    }
                }
            })

            // 카메라 버튼
            btnCamera.setOnClickListener {
                rootMediaLayout.expandAnimation(
                    Constants.DURATION_FADE_OUT,
                    Constants.MEDIA_RCV_HEIGHT
                )
                startActivityForResult(requireContext().openCameraIntent(), Constants.RC_GET_CAMERA)
            }

            // 미디어 스토어 버튼.
            btnMediaStore.setOnClickListener {
                rootMediaLayout.expandAnimation(
                    Constants.DURATION_FADE_OUT,
                    Constants.MEDIA_RCV_HEIGHT
                )
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
                boardDetailVM.getVedios().observeOnce(viewLifecycleOwner, observerMediaStoreVideo)
            }

            btnMediaImage.setOnClickListener {
                if (rcvMediaImageList.visibility == View.GONE) rcvMediaImageList.fadeInAnimation()
                if (rcvMediaAudioList.visibility == View.VISIBLE) rcvMediaAudioList.visibility =
                    View.GONE
                if (rcvMediaVideoList.visibility == View.VISIBLE) rcvMediaVideoList.visibility =
                    View.GONE
                boardDetailVM.getImages().observeOnce(viewLifecycleOwner, observerMediaStoreImage)
            }

            btnMediaRecord.setOnClickListener {
                if (rcvMediaAudioList.visibility == View.GONE) rcvMediaAudioList.fadeInAnimation()
                if (rcvMediaImageList.visibility == View.VISIBLE) rcvMediaImageList.visibility =
                    View.GONE
                if (rcvMediaVideoList.visibility == View.VISIBLE) rcvMediaVideoList.visibility =
                    View.GONE
                boardDetailVM.getAudios().observeOnce(viewLifecycleOwner, observerMediaStoreAudio)
            }

            // 새로고침 버튼.
            btnRefresh.setOnClickListener {
                boardDetailVM.apply {
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
                boardDetailVM.run {
                    getSelectedObjectList().value.run {
                        if (this.isNullOrEmpty()) rootMediaLayout.collapseAnimation(
                            Constants.DURATION_FADE_OUT,
                            0
                        )
                        else rootMediaAddedList.fadeInAnimation()
                    }
                }
            }

            // 리싸이클러뷰 뷰 범위 변경.
            btnTablePlus.setOnClickListener {
                var targetRangeCounter: Int = Constants.GALLERY_ITEM_RANGE
                when {
                    rcvMediaImageList.visibility == View.VISIBLE -> {
                        if (imageTableCounter < 5) ++imageTableCounter
                        else {
                            imageTableCounter = Constants.GALLERY_ITEM_RANGE
                        }
                        targetRangeCounter = imageTableCounter
                    }
                    rcvMediaAudioList.visibility == View.VISIBLE -> {
                        if (audioTableCounter < 5) ++audioTableCounter
                        else {
                            audioTableCounter = Constants.AUDIO_ITEM_RANGE
                        }
                        targetRangeCounter = audioTableCounter
                    }
                    rcvMediaVideoList.visibility == View.VISIBLE -> {
                        if (videoTableCounter < 5) ++videoTableCounter
                        else {
                            videoTableCounter = Constants.VIDEO_ITEM_RANGE
                        }
                        targetRangeCounter = videoTableCounter
                    }
                }
                changedReyclerItemRange(targetRangeCounter)
            }

/*
            btnSave.setOnClickListener {
                testSave(System.currentTimeMillis().toString())
            }

            btnRead.setOnClickListener {
                testRead()
            }
*/
        }
    }

    private fun setUpRecyclerView() {
        mBinding?.apply {
            rcvMediaImageList.apply {
                mediaImagePagedAdapter =
                    MediaImagePagedAdapter(context).apply { addListener(this@BoardDetailFragment) }
                layoutManager = GridLayoutManager(requireActivity(), Constants.GALLERY_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = mediaImagePagedAdapter
            }
            rcvMediaAudioList.apply {
                mediaAudioPagedAdapter =
                    MediaAudioPagedAdapter(context).apply { addListener(this@BoardDetailFragment) }
                layoutManager = GridLayoutManager(requireActivity(), Constants.GALLERY_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = mediaAudioPagedAdapter
            }

            rcvMediaVideoList.apply {
                mediaVideoPagedAdapter =
                    MediaVideoPagedAdapter(context).apply { addListener(this@BoardDetailFragment) }
                layoutManager = GridLayoutManager(requireActivity(), Constants.GALLERY_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = mediaVideoPagedAdapter
            }

            rcvMediaAdded.apply {
                selectedListAdapter =
                    SelectedMediaFileListAdapter(context).apply { addEventListener(this@BoardDetailFragment) }
                layoutManager = GridLayoutManager(requireActivity(), Constants.SELECTED_ITEM_RANGE)
                isNestedScrollingEnabled = true
                adapter = selectedListAdapter
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val contentResolver = context!!.contentResolver
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.RC_GET_CONTENT -> {
                    for (item in getExtraOrdinaryFiles(data)) {
                        context?.getDataFromContentUri(item)?.let { target ->
                            addSelectedMediaStoreItem(null, null, target, target.type, true)
                        }
                    }
                    mBinding?.rootMediaAddedList?.fadeInAnimation()
                }

                Constants.RC_GET_CAMERA -> {
                    when (data?.data) {
                        null -> true
                        else -> false
                    }.let {
                        when (it) {
                            true -> outputMediaFileUri
                            else -> {
                                outputMediaFileUri?.delete(contentResolver)
                                data?.data
                            }
                        }
                    }?.run {
                        context?.getDataFromContentUri(this)
                    }?.let {
                        // CAMERA: /storage/emulated/0/Pictures/20200820_041947_IMAGE.jpg
                        // IMAGE: /storage/emulated/0/Pictures/tUHEtWJ3R58.jpg
                        addSelectedMediaStoreItem(null, null, it, it.type, true)
                        mBinding?.rootMediaAddedList?.fadeInAnimation()
                    }
                }
                Constants.RC_GET_VIDEO -> {
                    val videoData = data?.data
                    videoData?.let { it ->
                        context?.getDataFromContentUri(it)?.let {
                            addSelectedMediaStoreItem(null, null, it, it.type, true)
                            mBinding?.rootMediaAddedList?.fadeInAnimation()
                        }
                    }
                }
            }
        } else if (resultCode == Activity.DEFAULT_KEYS_DISABLE) {
            if (requestCode == Constants.RC_GET_CAMERA)
                outputMediaFileUri?.delete(contentResolver)
            if (boardDetailVM.getAllSelectedItemList().isEmpty()) {
                mBinding?.rootMediaLayout?.collapseAnimation(Constants.DURATION_FADE_OUT, 0)
            }
        }
    }


    private fun getExtraOrdinaryFiles(paramIntent: Intent?): ArrayList<Uri> {
        val arrayList: ArrayList<Uri> = arrayListOf()
        if (paramIntent?.clipData != null) {
            paramIntent.clipData.let { clips ->
                val itemCount: Int? = clips?.itemCount
                itemCount?.let {
                    for (i in 0 until itemCount) {
                        arrayList.add(clips.getItemAt(i).uri)
                    }
                }
            }
        } else {
            paramIntent?.data?.let {
                arrayList.add(it)
            }
        }
        return arrayList
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
        binding: ViewDataBinding?,
        adapterPosition: Int?,
        item: MediaStoreItem,
        type: MediaStoreFileType,
        checked: Boolean?
    ) {
        val selectedItem: SelectedMediaStoreItem? =
            SelectedMediaStoreItem(
                binding,
                SelectedItem(adapterPosition, Uri.parse(item.contentUri), type, item)
            )
        when (checked) {
            true -> {
                boardDetailVM.addSelectedItem(selectedItem)
            }
            false -> {
                boardDetailVM.removeSelectedItem(selectedItem)
            }
        }

    }

    override fun onClickSelectedItem(selectedItem: SelectedMediaStoreItem) {
        startActivity(
            MediaStoreViewPagerActivity.getStartIntent(
                requireContext(),
                boardDetailVM.getAllSelectedItemList(),
                boardDetailVM.getSelectedIndexOf(selectedItem)
            )
        )
    }

    override fun onMediaAudioPlayClientEvent(item: MediaStoreAudio?) {
        val ft = parentFragmentManager.beginTransaction()
        val prev = parentFragmentManager.findFragmentByTag(Constants.TAG_AUDIO_DIALOG_FRAGMENT)
        prev?.let {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val audioPlayDialog = AudioPlayDialogFragment.newInstance(item)
        audioPlayDialog.show(ft, Constants.TAG_AUDIO_DIALOG_FRAGMENT)
    }

    override fun onMediaVideoPlayClientEvent(item: MediaStoreVideo?) {
        val mIntent = ExoPlayerActivity.getStartIntent(requireContext(), item)
        requireContext().startActivity(mIntent)
    }

    // 클릭했을 때 뭘 하나 덜지우면, 깜빡거린다.
    override fun onDeleteSelectedItem(target: SelectedMediaStoreItem) {
        boardDetailVM.run {
            target.selectedItem.position?.let { pos ->
                when (target.selectedItem.type) {
                    MediaStoreFileType.IMAGE -> mediaImagePagedAdapter.removeSelectedItem(pos)
                    MediaStoreFileType.AUDIO -> mediaAudioPagedAdapter.removeSelectedItem(pos)
                    MediaStoreFileType.VIDEO -> mediaVideoPagedAdapter.removeSelectedItem(pos)
                    else -> return
                }
            }
            removelSelectedItemAnimation(target.selectedItem)
            removeSelectedItem(target)
        }
    }

    override fun onPlayMediaItem(item: SelectedMediaStoreItem) {
        when (item.selectedItem.type) {
            MediaStoreFileType.AUDIO -> {
                onMediaAudioPlayClientEvent(item.selectedItem.item as MediaStoreAudio)
            }
            MediaStoreFileType.VIDEO -> {
                onMediaVideoPlayClientEvent(item.selectedItem.item as MediaStoreVideo)
            }
            else -> return
        }
    }

    override fun onOpenFile(item: SelectedMediaStoreItem) {
        (item.selectedItem.item as MediaStoreFile).run {
            requireContext().viewFile(Uri.parse(this.contentUri), this.displayName)
        }
    }

    inner class Thumbnail(private val target: SelectedMediaStoreItem) :
        LinearLayout(requireContext()) {
        init {
            mBinding?.thumbnailLyt?.removeAllViews()
            setThumbnail()
        }

        // addView 할때 match_parent & wrap_content 지켜야 함.
        private fun setThumbnail() {
            when (target.selectedItem.type) {
                MediaStoreFileType.IMAGE -> {
                    val binding: BoardItemMediaThumbnailImageBinding = DataBindingUtil.inflate(
                        requireActivity().layoutInflater,
                        R.layout.board_item_media_thumbnail_image,
                        this,
                        true
                    )

                    binding.run {
                        GlideImageLoader(ivThumbnailImage, null).load(
                            (target.selectedItem.contentUri.toString()),
                            ProgressAppGlideModule.requestOptions(requireActivity())
                        )
                        btnDelete.setOnClickListener {
                            onDeleteSelectedItem(target)
                        }
                        rootThumbnailLyt.setOnClickListener {
                            onClickSelectedItem(target)
                        }
                    }
                }
                MediaStoreFileType.AUDIO -> {
                    val binding: BoardItemMediaThumbnailAudioBinding = DataBindingUtil.inflate(
                        requireActivity().layoutInflater,
                        R.layout.board_item_media_thumbnail_audio,
                        this,
                        true
                    )
                    binding.run {
                        tvDuration.text = (target.selectedItem.item as MediaStoreAudio).duration
                        btnPlay.setOnClickListener {
                            onMediaAudioPlayClientEvent(target.selectedItem.item)
                        }
                        btnDelete.setOnClickListener {
                            onDeleteSelectedItem(target)
                        }
                        rootThumbnailLyt.setOnClickListener {
                            onClickSelectedItem(target)
                        }
                    }
                }
                MediaStoreFileType.VIDEO -> {
                    val binding: BoardItemMediaThumbnailVideoBinding = DataBindingUtil.inflate(
                        requireActivity().layoutInflater,
                        R.layout.board_item_media_thumbnail_video,
                        this,
                        true
                    )
                    binding.run {
                        tvDuration.text = (target.selectedItem.item as MediaStoreVideo).duration
                        GlideImageLoader(ivThumbnailImage, null).load(
                            (target.selectedItem.contentUri.toString()),
                            ProgressAppGlideModule.requestOptions(requireActivity())
                        )
                        btnPlay.setOnClickListener {
                            onMediaVideoPlayClientEvent(target.selectedItem.item as MediaStoreVideo)
                        }
                        btnDelete.setOnClickListener {
                            onDeleteSelectedItem(target)
                        }
                        rootThumbnailLyt.setOnClickListener {
                            onClickSelectedItem(target)
                        }
                    }
                }
                MediaStoreFileType.FILE -> {
                    val binding: BoardItemMediaThumbnailFileBinding = DataBindingUtil.inflate(
                        requireActivity().layoutInflater,
                        R.layout.board_item_media_thumbnail_file,
                        this,
                        true
                    )
                    binding.run {
                        tvDisplayName.text =
                            (target.selectedItem.item as MediaStoreFile).displayName
                        btnDelete.setOnClickListener {
                            onDeleteSelectedItem(target)
                        }
                        rootThumbnailLyt.setOnClickListener {
                            onClickSelectedItem(target)
                        }
                        btnOpen.setOnClickListener {
                            onOpenFile(target)
                        }
                    }
                }
            }
        }
    }


    /**
     * select한 아이템을 모두 저장하는 메소드
     *
     * - 모두 같은 디렉토리에 저장 됨
     * - 선택된 아이템의 uri은 content일 수도 있고, storage일 수도 있음.
     * - content로 읽은 uri은 copyContentUri()로 파일 복사.
     * - storage로 읽은 uri은 copyStorageUri()로 파일 복사.
     * - 결과적으로 모두 content uri로 저장되고, 이는 실제 경로가 아님.
     * @author 권혁신
     * @version 1.0.0
     * @since 2020-08-23 오후 2:43
     **/
    fun update() {
        val mediaItemList: ArrayList<MediaStoreItem>? = arrayListOf()
        for (target in boardDetailVM.getAllSelectedItemList()) {
            val item: MediaStoreItem = when (target.type) {
                MediaStoreFileType.IMAGE -> {
                    target.item as MediaStoreImage
                }
                MediaStoreFileType.VIDEO -> {
                    target.item as MediaStoreVideo
                }
                MediaStoreFileType.AUDIO -> {
                    target.item as MediaStoreAudio
                }
                MediaStoreFileType.FILE -> {
                    target.item as MediaStoreFile
                }
            }
            val isFileExist = requireContext().isFileExist(
                mNoteId.toString(),
                File(Uri.parse(item.contentUri)?.path).name
            )
            var savedUri: Uri? = null
            if (!isFileExist) {
                val scheme = Uri.parse(item.contentUri)?.scheme
                val tempFile = context?.createMediaFile(mNoteId.toString(), item)
                tempFile?.let {
                    savedUri = if (scheme.equals("content", ignoreCase = true))
                        context?.copyContentUri(Uri.parse(item.contentUri), it)
                    else
                        context?.copyStorageUri(Uri.parse(item.contentUri), it)
                }
            } else {
                savedUri = Uri.parse(item.contentUri)
            }
            mediaItemList?.add(
                MediaStoreItem(
                    item.id,
                    item.dateTaken,
                    item.displayName,
                    savedUri.toString(),
                    item.type
                )
            )
        }
        val content = mBinding?.edtContent?.text?.toString()?.trim()
        if (content.isNullOrEmpty() && mediaItemList.isNullOrEmpty()) {
            boardDetailVM.deleteNote(mNoteId)
            return
        }
        var thumbnail = if (mediaItemList?.size != 0) {
            mediaItemList?.get(0)?.contentUri
        } else {
            null
        }
        val note = LovelyNote(
            mNoteId,
            content,
            thumbnail,
            mediaItemList,
            mBinding?.note?.createTimeStamp,
            currentDate,
            isHold = false,
            isLock = false
        )
        boardDetailVM.updateNote(note)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        requireContext().clearCacheData()
        Timber.d("onDetach()")
    }

    override fun onBackPressed(): Boolean {
        mActivity.apply {
            toggleFab()
            changeFabImage(getDrawable(R.drawable.tag_plus))
            changeToolBar(getString(applicationInfo.labelRes), R.drawable.ic_baseline_menu)
        }
        parentFragmentManager.popBackStack(
            Constants.TAG_DETAIL_FRAGMENT,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        return true
    }
}