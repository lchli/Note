package com.lch.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing.model.entity.BaseMedia
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.EditNoteFragmentBinding
import com.materialstudies.owl.util.transition.MaterialContainerTransition

class NoteContentListFragment:BaseAppFragment() {
    private lateinit var binding: EditNoteFragmentBinding
    private val mEditNoteViewModel:EditNoteViewModel by viewModels()
    private lateinit var mNoteContentAdapter : NoteContentAdapter
    //private val args: NoteContentListFragmentArgs by navArgs()
    private var insertPosition:Int?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mNoteContentAdapter = NoteContentAdapter(mEditNoteViewModel)

        binding=  EditNoteFragmentBinding.inflate(inflater)

       // postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)
        val interp = AnimationUtils.loadInterpolator(
                context,
                android.R.interpolator.fast_out_slow_in
        )
        sharedElementEnterTransition = MaterialContainerTransition(R.id.root).apply {
            duration = 400L
            interpolator = interp
        }
//        enterTransition = DiagonalSlide().apply {
//            addTarget(R.id.lessons_sheet)
//            startDelay = 200L
//            duration = 200L
//            interpolator = interp
//        }
        sharedElementReturnTransition = MaterialContainerTransition().apply {
            duration = 300L
            interpolator = interp
        }
//        returnTransition = DiagonalSlide().apply {
//            addTarget(R.id.lessons_sheet)
//            duration = 100L
//            interpolator = interp
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter=mNoteContentAdapter

        mEditNoteViewModel.noteListEvent.observe(viewLifecycleOwner, {
            mNoteContentAdapter.setListData(it)
        })
        mEditNoteViewModel.inserImgEvent.observe(viewLifecycleOwner, {
            //openSystemImageChooser(1)
            insertPosition=it
            val singleImgConfig = BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
            Boxing.of(singleImgConfig).withIntent(requireContext(), BoxingActivity::class.java).start(this, 1)

        })
        mEditNoteViewModel.inserAudioEvent.observe(viewLifecycleOwner, {
            //openSystemImageChooser(1)
           // val singleImgConfig = BoxingConfig(BoxingConfig.Mode.AUDIO)
           // Boxing.of(singleImgConfig).withIntent(requireContext(), BoxingActivity::class.java).start(this, 1)

        })
        mEditNoteViewModel.inserVideooEvent.observe(viewLifecycleOwner, {
            //openSystemImageChooser(1)
           // val singleImgConfig = BoxingConfig(BoxingConfig.Mode.VIDEO)
            //Boxing.of(singleImgConfig).withIntent(requireContext(), BoxingActivity::class.java).start(this, 1)

        })
        mEditNoteViewModel.showOpEvent.observe(viewLifecycleOwner, {
            ItemListDialogFragment(mEditNoteViewModel,it).show(childFragmentManager, "")
        })

        mEditNoteViewModel.insertText(requireContext(), "333")

        binding.saveBtn.setOnClickListener {
            mEditNoteViewModel.save(requireContext())
            findNavController().navigateUp()
        }

        binding.moreBtn.setOnClickListener {
            ItemListDialogFragment(mEditNoteViewModel).show(childFragmentManager, "")
        }
    }

    private fun openSystemImageChooser(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
        // 选择视频: intent.type = "video/*";
        // 选择所有类型的资源: intent.type = "*/*"
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val medias: List<BaseMedia>? = Boxing.getResult(data)
            if (!medias.isNullOrEmpty()) {
                mEditNoteViewModel.insertImg(requireContext(), medias!![0].path,insertPosition)
                Log.e("sss", "uri:" +  medias!![0].path)

            }
           // val uri = data?.data ?: return

           // Log.e("sss", "uri:" + uri)

        }
    }

}