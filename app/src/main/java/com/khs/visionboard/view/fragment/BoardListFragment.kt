package com.khs.visionboard.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.khs.visionboard.R
import com.khs.visionboard.databinding.FragmentListBinding
import com.khs.visionboard.model.Board
import com.khs.visionboard.view.adapter.BoardListAdapter
import com.khs.visionboard.viewmodel.BoardListVM
import com.khs.visionboard.viewmodel.factory.FactoryBoardListVM
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class BoardListFragment : BaseFragment<FragmentListBinding>() {

    private var param1: String? = null
    private var param2: String? = null
    private var recyclerView: RecyclerView? = null
    private var listAdapter: BoardListAdapter? = null
    private lateinit var boardListVM: BoardListVM

    private val observer: Observer<List<Board>?> =
        Observer { boards: List<Board>? ->
            boards?.let{
                Timber.d(boards.size.toString())
                listAdapter?.submitList(boards)
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
        recyclerView = mBinding?.rcvBoardList
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        listAdapter = BoardListAdapter(context)
        recyclerView?.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.adapter = listAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        boardListVM = ViewModelProvider(this, FactoryBoardListVM(requireActivity().application, 100)).get(BoardListVM::class.java)
        boardListVM.getBoardList().observe(viewLifecycleOwner,observer)
        this.lifecycle.addObserver(boardListVM)
        mBinding?.btnAdd?.setOnClickListener {
/*            val temp = UUID.randomUUID().toString()
            var testBoard = Board(temp,temp,"설명",123)
            boardListVM.addBoard(testBoard)*/
            boardListVM.setBoard()
        }
        setAdapterListener()
    }

    private fun setAdapterListener() {
        listAdapter?.addEventListener(object : BoardListAdapter.BoardListEvent {

            override fun onClick(position: Int) {
                val item = boardListVM.getBoardItem(position)
                Toast.makeText(context,item?.boardId,Toast.LENGTH_SHORT).show()
            }

            override fun onDelete(position: Int) {
                boardListVM.deleteBoardItem(position)
            }

        })
    }

}