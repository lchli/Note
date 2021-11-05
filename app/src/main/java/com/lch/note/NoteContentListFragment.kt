package com.lch.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.blankj.utilcode.util.BarUtils
import com.lch.audio_player.LchAudioPlayer
import com.lch.cl.FileDetailVm
import com.lch.video_player.VideoPlayer
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.FileDetailUiBinding
import com.materialstudies.owl.ui.lessons.LessonFragmentArgs
import com.materialstudies.owl.util.transition.MaterialContainerTransition

class NoteContentListFragment:BaseAppFragment() {
    private lateinit var binding: FileDetailUiBinding
    private val mFileDetailVm: FileDetailVm by viewModels()
    private lateinit var mNoteContentAdapter : NoteContentAdapter
    //private val args: NoteContentListFragmentArgs by navArgs()
    private var insertPosition:Int?=null
    private val audioPlayer = LchAudioPlayer.newAudioPlayer()
    private lateinit var videoPlayer: VideoPlayer
    private val args: NoteContentListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=  FileDetailUiBinding.inflate(inflater)
        binding.statusBar.apply {
            layoutParams.height = BarUtils.getStatusBarHeight()
        }

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
        binding.lifecycleOwner=viewLifecycleOwner
        binding.state=mFileDetailVm.state

        mFileDetailVm.load(args.filePath)

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

}