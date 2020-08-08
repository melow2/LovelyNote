package com.khs.visionboard.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.khs.visionboard.R
import com.khs.visionboard.databinding.FragmentAddBoardBinding
import timber.log.Timber


class AddBoardFragment : BaseFragment<FragmentAddBoardBinding>(){

    private var param1: String? = null
    private var param2: String? = null

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_add_menu,menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListener()
    }

    private fun setUpListener() {
        mBinding?.btnImageAdd?.setOnClickListener {
            showPopup(it)
        }
    }

    private fun showPopup(v:View){
        val popup = PopupMenu(requireActivity(),v)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_type_gallery -> {
                    Toast.makeText(requireActivity(),"DDDDD",Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.inflate(R.menu.fragment_add_image_popup_menu)
        popup.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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