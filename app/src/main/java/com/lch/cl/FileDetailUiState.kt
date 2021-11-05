package com.lch.cl

import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.blankj.utilcode.util.TimeUtils
import com.materialstudies.owl.BuildConfig
import com.materialstudies.owl.R
import com.materialstudies.owl.ui.mycourses.MyCoursesFragmentDirections
import com.materialstudies.owl.util.log
import java.io.File

class FileDetailUiState(val filePath: String, val vm: FileDetailVm) {

    fun itemClick(view: View) {
        val uri = FileProvider.getUriForFile(
            view.context,
            "${BuildConfig.APPLICATION_ID}.file.provider", File(filePath)
        )
        val type = view.context.contentResolver.getType(uri)
        log("mime:$type")


        val extras = FragmentNavigatorExtras(
            view to "shared_element"
        )
        val action = MyCoursesFragmentDirections.actionOnboardingToLearn("courseId")
        view.findNavController().navigate(action, extras)
    }

    val fileName: String? by lazy {
        return@lazy File(filePath).name
    }

    val fileDate: String? by lazy {

        return@lazy TimeUtils.getFriendlyTimeSpanByNow(File(filePath).lastModified())
    }

    val fileSize: String? by lazy {
        return@lazy CommonUtil.formatFileSize(File(filePath).length(), false)
    }

    val typeIcon: Drawable? by lazy {
        val f = File(filePath)
        if (Mime.Video.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_video)
        } else if (Mime.Audio.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_audio)
        } else if (Mime.Img.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_img)
        } else if (Mime.Zip.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_zip)
        } else if (Mime.Pdf.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_pdf)
        } else if (Mime.Doc.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_doc)
        }

        return@lazy vm.context?.getDrawable(R.drawable.mime_other)
    }
}