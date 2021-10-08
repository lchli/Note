package com.lch.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing.model.entity.BaseMedia
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.materialstudies.owl.databinding.FragmentItemListDialogListDialogBinding


// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ItemListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class ItemListDialogFragment(val mEditNoteViewModel: EditNoteViewModel,val position:Int?=null) : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentItemListDialogListDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentItemListDialogListDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.insertText.setOnClickListener {
            mEditNoteViewModel.insertText(requireContext(), "",position )
            dismissAllowingStateLoss()
        }

          binding.insertImg.setOnClickListener {

             mEditNoteViewModel.onInsertImg(position)

            dismissAllowingStateLoss()
        }
        binding.insertAudio.setOnClickListener {

            //mEditNoteViewModel.onInsertImg(position)

            dismissAllowingStateLoss()
        }

        binding.insertVideo.setOnClickListener {

            //mEditNoteViewModel.onInsertImg(position)

            dismissAllowingStateLoss()
        }


    }






}