package com.lch.note

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.text.TextUtils
import android.view.*
import android.view.TextureView.SurfaceTextureListener
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.lch.audio_player.AudioPlayer
import com.lch.note.*
import com.lch.video_player.VideoPlayer
import com.lch.video_player.helper.VideoHelper
import com.lch.video_player.model.BAFTimedText
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.NoteContentAudioItemBinding
import com.materialstudies.owl.databinding.NoteContentImgItemBinding
import com.materialstudies.owl.databinding.NoteContentTextItemBinding
import com.materialstudies.owl.databinding.NoteContentVideoItemBinding
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.util.*
import kotlin.collections.ArrayList

class NoteContentAdapter(
    private val mEditNoteViewModel: EditNoteViewModel,
    private val mAudioPlayer: AudioPlayer,
    private val mVideoPlayer: VideoPlayer,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listData: List<Any>? = null
    private var currentPlayingAudioPosition = -1
    private var currentPlayingVideoPosition = -1
    private var videoRotation = -1
    private var isShowLoading = false
    private var isVideoSizeChanged = false
    private var isVideoRotationChanged = false
    private val textureSparseArray = HashMap<Int, SurfaceTexture>()

    init {

        mAudioPlayer.setStateListener(object : AudioPlayer.StateListener() {
            override fun onError(what: Int, extra: Int) {
                ToastUtils.showLong("播放失败！")
                notifyDataSetChanged()
            }

            override fun onCompletion() {
                super.onCompletion()
                notifyDataSetChanged()
            }

            override fun onProgress(currentPositionMillsecs: Long, totalMillsecs: Long) {
                super.onProgress(currentPositionMillsecs, totalMillsecs)
                notifyDataSetChanged()
            }

            override fun onPrepared() {
                mAudioPlayer.start()
                notifyDataSetChanged()
            }
        })

        mVideoPlayer.setStateListener(object : VideoPlayer.StateListener() {
            override fun onError(what: Int, extra: Int) {
                isShowLoading = false
                notifyDataSetChanged()
            }

            override fun onCompletion() {
                VideoHelper.releaseAudioFocus(context)
                notifyDataSetChanged()
            }

            override fun onBufferingUpdate(percent: Int) {
                notifyDataSetChanged()
            }

            override fun onProgress(currentPositionMillsecs: Long, totalMillsecs: Long) {
                notifyDataSetChanged()
            }

            override fun onVideoSizeChanged(width: Int, height: Int) {
                isVideoSizeChanged = true
                notifyDataSetChanged()
            }

            override fun onTimedText(text: BAFTimedText) {}
            override fun onPrepared() {
                isShowLoading = false
                isVideoSizeChanged = true
                VideoHelper.requestAudioFocus(context)
                mVideoPlayer.start()
                notifyDataSetChanged()
            }

            override fun onInfo(what: Int, extra: Int) {
                when (what) {
                    IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED -> {
                        videoRotation = extra
                        isVideoRotationChanged = true
                        notifyDataSetChanged()
                    }
                    IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        isShowLoading = true
                        notifyDataSetChanged()
                    }
                    IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        isShowLoading = false
                        notifyDataSetChanged()
                    }
                }
            }

            override fun onSeekComplete() {
                notifyDataSetChanged()
            }
        })
    }

    fun setListData(datas: List<Any>) {
        listData = ArrayList(datas)
        //notifyItemRangeChanged(0,listData?.size?:0)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.note_content_img_item -> return ImgHolder(
                NoteContentImgItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            R.layout.note_content_audio_item -> return AudioHolder(
                NoteContentAudioItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            R.layout.note_content_video_item -> return VideoHolder(
                NoteContentVideoItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            else -> return TextHolder(NoteContentTextItemBinding.inflate(LayoutInflater.from(parent.context)))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = listData?.get(position) ?: return
        when (holder) {
            is TextHolder -> {
                holder.bind(data as? NoteContentText, position)
            }

            is ImgHolder -> {
                holder.bind(data as? NoteContentImg, position)
            }
            is AudioHolder -> {
                holder.bind(data as? NoteContentAudio, position)
            }
            is VideoHolder -> {
                holder.bind(data as? NoteContentVideo, position)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val def = R.layout.note_content_text_item

        if (listData.isNullOrEmpty()) {
            return def
        }
        if (position < 0 || position >= listData!!.size) {
            return def
        }
        val data = listData?.get(position) ?: return def
        if (data is NoteContentImg) {
            return R.layout.note_content_img_item
        }
        if (data is NoteContentAudio) {
            return R.layout.note_content_audio_item
        }
        if (data is NoteContentVideo) {
            return R.layout.note_content_video_item
        }

        return def

    }

    override fun getItemCount(): Int {
        return listData?.size ?: 0
    }


    inner class TextHolder(val binding: NoteContentTextItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(data: NoteContentText?, position: Int) {
            data?.apply {
                binding.edit.setText(text)
                binding.moreOp.setOnClickListener {
                    showMenu(it, R.menu.content_ele_op, position)
                }
            }

        }

    }

    inner class ImgHolder(val binding: NoteContentImgItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteContentImg?, position: Int) {
            data?.apply {
                //binding.img.setImageURI(Uri.parse(resPath))
                Glide.with(binding.img).load(resPath).into(binding.img)
                binding.moreOp.setOnClickListener {
                    showMenu(it, R.menu.content_ele_op, position)
                }
            }

        }

    }

    inner class AudioHolder(val binding: NoteContentAudioItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(data: NoteContentAudio?, position: Int) {
            data?.apply {
                binding.apply {
                    simpleAudioView.prev.visibility = View.INVISIBLE
                    simpleAudioView.next.visibility = View.INVISIBLE
                    moreOp.setOnClickListener {
                        showMenu(it, R.menu.content_ele_op, position)
                    }

                    if (currentPlayingAudioPosition == position) {
                        if (mAudioPlayer.isPlaying()) {
                            simpleAudioView.ivPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                        } else {
                            simpleAudioView.ivPlayPause.setImageResource(android.R.drawable.ic_media_play)
                        }
                        val f: Float =
                            mAudioPlayer.getCurrentPosition().toFloat() / mAudioPlayer.getDuration()
                        simpleAudioView.seekBar.setProgress((simpleAudioView.seekBar.getMax() * f).toInt())
                        simpleAudioView.endText.setText(
                            VideoHelper.formatVideoTime(
                                mAudioPlayer.getDuration().toInt()
                            )
                        )
                        simpleAudioView.startText.setText(
                            VideoHelper.formatVideoTime(
                                mAudioPlayer.getCurrentPosition().toInt()
                            )
                        )
                    } else {
                        simpleAudioView.ivPlayPause.setImageResource(android.R.drawable.ic_media_play)
                        simpleAudioView.seekBar.setProgress(0)
                        simpleAudioView.endText.setText("00:00")
                        simpleAudioView.startText.setText("00:00")
                    }

                    simpleAudioView.ivPlayPause.setOnClickListener(View.OnClickListener {
                        if (currentPlayingAudioPosition == position) {
                            if (mAudioPlayer.isPlaying()) {
                                mAudioPlayer.pause()
                                simpleAudioView.ivPlayPause.setImageResource(android.R.drawable.ic_media_play)
                            } else if (mAudioPlayer.isPrepared()) {
                                mAudioPlayer.start()
                                simpleAudioView.ivPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                            } else {
                                mAudioPlayer.prepareAsync()
                            }
                        } else {
                            mAudioPlayer.reset()
                            mAudioPlayer.setDataSource(it.context, Uri.parse(data.resPath))
                            currentPlayingAudioPosition = position
                            mAudioPlayer.prepareAsync()
                        }
                    })

                    simpleAudioView.seekBar.setEnabled(currentPlayingAudioPosition == position)

                    simpleAudioView.seekBar.setOnSeekBarChangeListener(object :
                        OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            if (seekBar.isEnabled) {
                                val f = seekBar.progress.toFloat() / seekBar.max
                                mAudioPlayer.seekTo((f * mAudioPlayer.getDuration()).toInt())
                            }
                        }
                    })

                }
            }

        }

    }

    inner class VideoHolder(val binding: NoteContentVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteContentVideo?, position: Int) {
            data?.apply {
                binding.apply {
                    videoView.playerController.ivNext.visibility = View.GONE
                    videoView.playerController.ivPre.visibility = View.GONE
                    videoView.playerController.ivBackward.visibility = View.GONE
                    videoView.playerController.ivForward.visibility = View.GONE
                    moreOp.setOnClickListener {
                        showMenu(it, R.menu.content_ele_op, position)
                    }



                    videoView.getPlayerController().seekBar.setEnabled(currentPlayingVideoPosition == position)

                    videoView.getPlayerController().seekBar.setOnSeekBarChangeListener(object :
                        OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            if (seekBar.isEnabled) {
                                val f = seekBar.progress.toFloat() / seekBar.max
                                mVideoPlayer.seekTo((f * mVideoPlayer.getDuration()).toInt())
                            }
                        }
                    })

                    videoView.getRenderView()
                        .setSurfaceTextureListener(object : SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                                textureSparseArray.put(position, surface)
                                if (position == currentPlayingVideoPosition) {
                                    mVideoPlayer.setSurface(Surface(surface))
                                    isVideoSizeChanged = true
                                    isVideoRotationChanged = true
                                    notifyDataSetChanged()
                                }
                            }

                            override fun onSurfaceTextureSizeChanged(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                            }

                            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                textureSparseArray.remove(position)
                                if (position == currentPlayingVideoPosition) {
                                    mVideoPlayer.setSurface(null)
                                }
                                return false
                            }

                            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                        })


                    if (position == currentPlayingVideoPosition) {
                        if (mVideoPlayer.isPlaying()) {
                            videoView.getPlayerController().ivPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                        } else {
                            videoView.getPlayerController().ivPlayPause.setImageResource(android.R.drawable.ic_media_play)
                        }
                        val f: Float =
                            mVideoPlayer.getCurrentPosition().toFloat() / mVideoPlayer.getDuration()
                        videoView.getPlayerController()
                            .setProgress(
                                (videoView.getPlayerController().getMaxProgress() * f).toInt()
                            )
                        videoView.getPlayerController()
                            .setCurrentTime(VideoHelper.formatVideoTime(mVideoPlayer.getCurrentPosition().toInt()))
                        if (isShowLoading) {
                            isShowLoading = false
                            videoView.showLoading()
                        } else {
                            videoView.hideLoading()
                        }
                        val per = mVideoPlayer.getBufferPercent().toFloat() / 100
                        videoView.getPlayerController().setSecondProgress(
                            (videoView.getPlayerController().getMaxProgress() * per) .toInt()
                        )
                        if (isVideoSizeChanged) {
                            isVideoSizeChanged = false
                            if (VideoHelper.isAcceptVideoSize(
                                    mVideoPlayer.getVideoWidth(),
                                    mVideoPlayer.getVideoHeight()
                                )
                            ) {
                                videoView.setVideoSize(
                                    mVideoPlayer.getVideoWidth(),
                                    mVideoPlayer.getVideoHeight()
                                )
                            }
                        }
                        if (isVideoRotationChanged) {
                            isVideoRotationChanged = false
                            videoView.setVideoRotation(videoRotation)
                        }
                        videoView.getPlayerController()
                            .setTotalTime(VideoHelper.formatVideoTime(mVideoPlayer.getDuration().toInt()))
                    } else {
                        videoView.getPlayerController().resetUI()
                        videoView.getPlayerController().setVisibility(View.VISIBLE)
                    }


                    videoView.getPlayerController().ivPlayPause.setOnClickListener(View.OnClickListener {
                        if (position == currentPlayingVideoPosition) {
                            if (mVideoPlayer.isPlaying()) {
                                mVideoPlayer.pause()
                                videoView.getPlayerController().ivPlayPause.setImageResource(
                                    android.R.drawable.ic_media_play
                                )
                            } else if (mVideoPlayer.isPrepared()) {
                                mVideoPlayer.start()
                                videoView.getPlayerController().ivPlayPause.setImageResource(
                                    android.R.drawable.ic_media_pause
                                )
                            } else {
                                mVideoPlayer.prepareAsync()
                            }
                        } else {
                            val texture: SurfaceTexture? = textureSparseArray.get(position)
                            if (texture == null) {
                                ToastUtils.showShort("texture未准备好！")
                                return@OnClickListener
                            }
                            if (data.resPath == null) {
                                ToastUtils.showShort("播放地址为空！")
                                return@OnClickListener
                            }
                            var useCache = false
                            if (data.resPath.startsWith("http://") || data.resPath.startsWith("https://")) {
                                useCache = true
                            }
                            mVideoPlayer.reset()
                            mVideoPlayer.setDataSource(
                                it.context,
                                Uri.parse(data.resPath),
                                useCache
                            )
                            currentPlayingVideoPosition = position
                            mVideoPlayer.setSurface(Surface(texture))
                            mVideoPlayer.prepareAsync()
                        }
                    })

                    videoView.getPlayerController().ivFullscreen.setOnClickListener(View.OnClickListener { v ->
                        if (!TextUtils.isEmpty(data.resPath)) {
                            mVideoPlayer.pause()
                            // FullVideoPlayActivity.launch(v.context, data.path)
                        }
                    })
                }
            }

        }

    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, position: Int) {
        val popup = PopupMenu(v.context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_del -> {
                    onDel(position)
                    mEditNoteViewModel.del(v.context, position)
                }
                R.id.option_after -> {
                    mEditNoteViewModel.insertAfter(position + 1)
                }
                R.id.option_before -> {
                    mEditNoteViewModel.insertBefore(position)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }


    private fun onDel(position: Int) {
        val viewType = getItemViewType(position)

        if (viewType == R.layout.note_content_video_item) {
            if (mVideoPlayer.isPlaying) {
                mVideoPlayer.reset()
            }
        }

        if (viewType == R.layout.note_content_audio_item) {
            if (mAudioPlayer.isPlaying) {
                mAudioPlayer.reset()
            }
        }
    }


}