package com.khs.lovelynote.view.fragment

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.FragmentListBinding
import com.khs.lovelynote.extension.Constants
import com.khs.lovelynote.extension.clearDirData
import com.khs.lovelynote.extension.getMediaMetaData
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.model.mediastore.*
import com.khs.lovelynote.view.adapter.RecyclerItemTouchHelper
import com.khs.lovelynote.view.activity.MainActivity
import com.khs.lovelynote.view.activity.MediaStoreViewPagerActivity
import com.khs.lovelynote.view.adapter.LovelyNoteListAdapter
import com.khs.lovelynote.viewmodel.BoardListVM
import com.khs.lovelynote.viewmodel.factory.BoardListVMFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class BoardListFragment : BaseFragment<FragmentListBinding>(),
    LovelyNoteListAdapter.LovelyNoteEventListener,
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private var param1: String? = null
    private var param2: String? = null
    private var mActivity: MainActivity? = null
    var listAdapter: LovelyNoteListAdapter? = null
    private lateinit var boardListVM: BoardListVM
    private val handler = Handler(Looper.getMainLooper())
    private val mScope = CoroutineScope(Dispatchers.Main)

    private val observer: Observer<List<LovelyNote>?> =
        Observer { notes: List<LovelyNote>? ->
            notes?.let {
                listAdapter?.apply {
                    modifyList(it)
                    val currentSize = this.currentList.size
                    val nextSize = it.size
                    if (currentSize < nextSize) {
                        handler.postDelayed({
                            mBinding?.rcvBoardList?.smoothScrollToPosition(0)
                        }, 100)
                    }
                }
            }
        }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BoardListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindView(inflater, container!!, R.layout.fragment_list)
        mActivity = activity as MainActivity
        return mBinding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        listAdapter =
            LovelyNoteListAdapter(context).apply { addEventListener(this@BoardListFragment) }
        mBinding?.rcvBoardList?.run {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = listAdapter
            setHasFixedSize(true) // 깜빡임 제거.
            val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
                RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this@BoardListFragment)
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        boardListVM =
            ViewModelProvider(this, BoardListVMFactory(requireActivity().application, 100)).get(
                BoardListVM::class.java
            )
        boardListVM.getNoteList()?.observe(viewLifecycleOwner, observer)
        this.lifecycle.addObserver(boardListVM)
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is LovelyNoteListAdapter.NoteViewHolder) {
            val item = listAdapter?.currentList?.get(viewHolder.adapterPosition)
            item?.apply {
                mScope.launch {
                    withContext(Dispatchers.IO) {
                        boardListVM.removeItem(this@apply)
                    }
                }
                val marginSide = 0
                val marginBottom = 265
                val snackbar = Snackbar.make(
                    requireView().rootView.findViewById(R.id.root_main_lyt),
                    "Item was removed from the list.",
                    Snackbar.LENGTH_LONG
                )
                val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
                params.gravity = Gravity.BOTTOM
               /* params.setMargins(
                    params.leftMargin + marginSide,
                    params.topMargin,
                    params.rightMargin + marginSide,
                    params.bottomMargin + marginBottom
                )*/
                snackbar.apply {
                    view.layoutParams = params
                    setAction("UNDO") {
                        mScope.launch {
                            withContext(Dispatchers.IO) {
                                boardListVM.insertItem(item)
                            }
                        }
                        handler.postDelayed({
                            mBinding?.rcvBoardList?.smoothScrollToPosition(position)
                        }, 100)
                    }
                    addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if(event == DISMISS_EVENT_TIMEOUT)
                                requireContext().clearDirData(item.Id.toString())
                        }
                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                        }
                    })
                }.show()
            }
        }
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView()")
        super.onDestroyView()
    }

    override fun onDetach() {
        Timber.d("onDetach()")
        super.onDetach()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onClick(note: LovelyNote, ivThumbnailImage: ImageView) {
        val ft = parentFragmentManager.beginTransaction()
        val detailFragment = BoardDetailFragment.newInstance(note.Id!!, note.mediaItems?.size!!)
        ft.apply {
            replace(
                R.id.fragment_main_container,
                detailFragment,
                Constants.TAG_DETAIL_FRAGMENT
            )
            addToBackStack(Constants.TAG_DETAIL_FRAGMENT)
            setReorderingAllowed(true) // 트랜지션 최적화
            commitAllowingStateLoss()
        }
        mActivity?.apply {
            toggleFab()
            changeFabImage(getDrawable(R.drawable.ic_baseline_create_24))
            changeToolBar("Modify", R.drawable.ic_baseline_arrow_back_24)
        }
    }

    override fun onClick(currentList: List<MediaStoreItem>?) {
        val list = toListSelectedItem(currentList)
        if(!list.isNullOrEmpty()) {
            startActivity(
                MediaStoreViewPagerActivity.getStartIntent(
                    context,
                    toListSelectedItem(currentList),
                    0
                )
            )
        }
    }

    private fun toListSelectedItem(list: List<MediaStoreItem>?): ArrayList<SelectedItem> {
        val itemList = ArrayList<SelectedItem>()
        list?.map {
            var mediaStoreItem: MediaStoreItem? = null
            when (it.type) {
                MediaStoreFileType.IMAGE -> {
                    mediaStoreItem = MediaStoreImage(
                        it.id, // id never be null
                        it.dateTaken,
                        it.displayName,
                        it.contentUri,
                        it.type
                    )
                }
                MediaStoreFileType.AUDIO -> {
                    mediaStoreItem = MediaStoreAudio(
                        it.id, // id never be null
                        it.dateTaken,
                        it.displayName ?: Constants.TAG_RECORD,
                        it.contentUri,
                        it.type,
                        context?.getMediaMetaData(
                            Uri.parse(it.contentUri),
                            MediaMetadataRetriever.METADATA_KEY_ALBUM
                        ) ?: Constants.TAG_RECORD,
                        context?.getMediaMetaData(
                            Uri.parse(it.contentUri),
                            MediaMetadataRetriever.METADATA_KEY_TITLE
                        ) ?: Constants.TAG_RECORD,
                        context?.getMediaMetaData(
                            Uri.parse(it.contentUri),
                            MediaMetadataRetriever.METADATA_KEY_DURATION
                        )
                    )
                }
                MediaStoreFileType.VIDEO -> {
                    mediaStoreItem= MediaStoreVideo(
                        it.id, // id never be null
                        it.dateTaken,
                        it.displayName,
                        it.contentUri,
                        it.type,
                        context?.getMediaMetaData(
                            Uri.parse(it.contentUri),
                            MediaMetadataRetriever.METADATA_KEY_DURATION
                        )
                    )
                }
                MediaStoreFileType.FILE -> {
                    mediaStoreItem= MediaStoreFile(
                        it.id, // id never be null
                        it.dateTaken,
                        it.displayName,
                        it.contentUri,
                        it.type
                    )
                }
            }
            itemList.add(
                SelectedItem(
                    null,
                    Uri.parse(mediaStoreItem?.contentUri),
                    mediaStoreItem?.type!!,
                    mediaStoreItem
                )
            )
        }
        return itemList
    }
}